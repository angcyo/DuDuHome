package com.dudu.android.launcher.ui.activity.bluetooth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.aios.ui.bt.CallRecord;
import com.dudu.aios.ui.bt.Contact;
import com.dudu.aios.ui.dialog.DeleteContactDialog;
import com.dudu.aios.ui.dialog.ShowContactDetailDialog;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.cache.AsyncTask;

import java.util.ArrayList;
import java.util.List;


public class BtContactsActivity extends BaseActivity implements OnClickListener {

    private ImageButton mButtonBack;

    private ImageButton mAddContacts;

    private ListView mRecordListView, mContactsListView;

    private RecordAdapter mRecordAdapter;

    private ContactsAdapter mContactsAdapter;

    private ArrayList<CallRecord> mRecordData;

    private ArrayList<Contact> mContactsData;

    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initOnListener();
        initData();
    }

    private void initData() {
        dbHelper = DbHelper.getDbHelper();

        mRecordData = new ArrayList<>();
        mRecordAdapter = new RecordAdapter(this, mRecordData);
        mRecordListView.setAdapter(mRecordAdapter);

        mContactsData = new ArrayList<>();
        mContactsAdapter = new ContactsAdapter(this, mContactsData);
        mContactsListView.setAdapter(mContactsAdapter);

        new LoadBtTask().execute();

    }

    private List<Contact> obtainContacts() {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
        int contactIdIndex = 0;
        int nameIndex = 0;

        if (cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            Log.i("phone", contactId);
            Log.i("phone", name);

            /*
             * 查找该联系人的phone信息
             */
            Cursor phones = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null, null);
            int phoneIndex = 0;
            if (phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex);
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(contactId));
                contact.setName(name);
                contact.setNumber(phoneNumber);
                Log.i("phone", phoneNumber);
                contacts.add(contact);
            }
        }
        return contacts;
    }

    private void initOnListener() {
        mButtonBack.setOnClickListener(this);
        mAddContacts.setOnClickListener(this);
        mContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent("wld.btphone.bluetooth.DIAL");
                String number = mContactsData.get(position).getNumber().replace("-", "");
                intent.putExtra("dial_number", number);
                sendBroadcast(intent);
                startActivity(new Intent(BtContactsActivity.this, BtCallingActivity.class).putExtra("number", getPhoneNumber(number)));
            }
        });
    }

    private String getPhoneNumber(String iNumber) {
        String number = iNumber.substring(0, 3) + " " + iNumber.substring(3, 7) + " " + iNumber.substring(7, 11);
        return number;
    }

    private void initView() {
        mButtonBack = (ImageButton) findViewById(R.id.button_back);
        mAddContacts = (ImageButton) findViewById(R.id.button_add_contacts);
        mRecordListView = (ListView) findViewById(R.id.listView_call_record);
        mContactsListView = (ListView) findViewById(R.id.listView_contacts);
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_bt_contacts, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                finish();
                break;
            case R.id.button_add_contacts:
                actionAdd();
                break;
        }
    }

    private void actionAdd() {
        showAddContactDialog();
    }

    private void showAddContactDialog() {
        ShowContactDetailDialog detailDialog = new ShowContactDetailDialog(this);
        detailDialog.show();
        detailDialog.setOnAddContactListener(new ShowContactDetailDialog.OnAddContactListener() {
            @Override
            public void saveContact(Contact contact) {
                dbHelper.insertContact(contact.getName(), contact.getNumber());
                mContactsData.add(contact);
                mContactsAdapter.setData(mContactsData);
            }
        });

    }

    class LoadBtTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            loadCallRecordData();
            loadContactsData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mRecordAdapter.setData(mRecordData);
            mContactsAdapter.setData(mContactsData);
        }
    }

    private void loadContactsData() {
        List<Contact> list = obtainContacts();
        if (list != null && list.size() != 0) {
            mContactsData.addAll(list);
        }

    }

    private void loadCallRecordData() {
        List<CallRecord> list = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        if (cursor.getCount() <= 0) {
            return;
        }
        cursor.moveToFirst();
        do {

            CallRecord record = new CallRecord();

            String nameTemp = cursor.getString(cursor
                    .getColumnIndex(CallLog.Calls.CACHED_NAME));
            if (nameTemp == null) {
                nameTemp = "";
            }

            if ("".equals(nameTemp)) {
                record.setName("");
            } else {
                record.setName(nameTemp);
            }

            record.setTime(cursor.getLong(cursor
                    .getColumnIndex(CallLog.Calls.DATE)));

            record.setDuration(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION)));

            record.setState(cursor
                    .getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));

            record.setNumber(cursor.getString(cursor
                    .getColumnIndex(CallLog.Calls.NUMBER)));

            list.add(record);
        } while (cursor.moveToNext());
        LogUtils.v("calls", "  " + list.size());
        mRecordData.addAll(list);
    }
}

class RecordAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<CallRecord> data;

    private LayoutInflater inflater;

    public RecordAdapter(Context context, ArrayList<CallRecord> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<CallRecord> data) {
        this.data = (ArrayList<CallRecord>) data.clone();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.bt_record_item, parent, false);
            holder.tvName = (TextView) convertView.findViewById(R.id.call_name);
            holder.tvType = (TextView) convertView.findViewById(R.id.call_state);
            holder.tvTime = (TextView) convertView.findViewById(R.id.call_time);
            holder.tvDuration = (TextView) convertView.findViewById(R.id.call_duration);
            holder.btnAdd = (ImageButton) convertView.findViewById(R.id.button_add);
            holder.btnDelete = (ImageButton) convertView.findViewById(R.id.button_delete);
            holder.btnEdit = (ImageButton) convertView.findViewById(R.id.button_edit);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CallRecord callRecord = data.get(position);
        holder.tvName.setText(callRecord.getName());
        if (callRecord.getState() == 0) {
            holder.tvType.setText("未接来电");
        } else if (callRecord.getState() == 1) {
            holder.tvType.setText("已接来电");
        }
        holder.tvTime.setText(String.valueOf(callRecord.getTime()));
        holder.tvDuration.setText(String.valueOf(callRecord.getDuration()));
        return convertView;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvType;
        TextView tvTime;
        TextView tvDuration;
        ImageButton btnAdd;
        ImageButton btnEdit;
        ImageButton btnDelete;
    }
}

class ContactsAdapter extends BaseAdapter {

    private ArrayList<Contact> data;

    private Context context;

    private LayoutInflater inflater;

    public ContactsAdapter(Context context, ArrayList<Contact> data) {
        this.data = data;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<Contact> data) {
        this.data = (ArrayList<Contact>) data.clone();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.bt_contacts_item, parent, false);
            holder.tvName = (TextView) convertView.findViewById(R.id.contacts_name);
            holder.tvNumber = (TextView) convertView.findViewById(R.id.contacts_number);
            holder.btnDetails = (ImageButton) convertView.findViewById(R.id.button_details);
            holder.btnDelete = (ImageButton) convertView.findViewById(R.id.button_delete);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Contact contacts = data.get(position);
        holder.tvName.setText(contacts.getName());
        holder.tvNumber.setText(contacts.getNumber());
        holder.btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteContactDialog(data.get(position));
            }
        });
        holder.btnDetails.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    private void showDeleteContactDialog(Contact contact) {
        DeleteContactDialog dialog = new DeleteContactDialog(context);
        dialog.show();
        dialog.setOnDialogButtonClickListener(new DeleteContactDialog.OnDialogButtonClickListener() {
            @Override
            public void onConfirmClick() {
                data.remove(contact);
               /* mContactsData.remove(contact);
                dbHelper.deleteContact(contact);*/
                notifyDataSetChanged();
            }
        });
    }

    class ViewHolder {
        TextView tvName;
        TextView tvNumber;
        ImageButton btnDetails;
        ImageButton btnDelete;
    }

}



