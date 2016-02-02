/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Y:\\ct\\q500\\Q500ML\\src\\LINUX\\android\\packages\\apps\\BtPhone\\src\\wld\\btphone\\bluetooth\\aidl\\PbapService.aidl
 */
package wld.btphone.bluetooth.aidl;

public interface PbapService extends android.os.IInterface {
	/** Local-side IPC implementation stub class. */
	public static abstract class Stub extends android.os.Binder implements
			PbapService {
		private static final String DESCRIPTOR = "wld.btphone.bluetooth.aidl.PbapService";

		/** Construct the stub at attach it to the interface. */
		public Stub() {
			this.attachInterface(this, DESCRIPTOR);
		}

		/**
		 * Cast an IBinder object into an wld.btphone.bluetooth.aidl.PbapService
		 * interface, generating a proxy if needed.
		 */
		public static PbapService asInterface(
				android.os.IBinder obj) {
			if ((obj == null)) {
				return null;
			}
			android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
			if (((iin != null) && (iin instanceof PbapService))) {
				return ((PbapService) iin);
			}
			return new Proxy(obj);
		}

		@Override
		public android.os.IBinder asBinder() {
			return this;
		}

		@Override
		public boolean onTransact(int code, android.os.Parcel data,
				android.os.Parcel reply, int flags)
				throws android.os.RemoteException {
			switch (code) {
			case INTERFACE_TRANSACTION: {
				reply.writeString(DESCRIPTOR);
				return true;
			}
			case TRANSACTION_PullphonebookStub: {
				data.enforceInterface(DESCRIPTOR);
				this.PullphonebookStub();
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_PullOutCallsStub: {
				data.enforceInterface(DESCRIPTOR);
				this.PullOutCallsStub();
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_PullInCallsStub: {
				data.enforceInterface(DESCRIPTOR);
				this.PullInCallsStub();
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_setDeviceStub: {
				data.enforceInterface(DESCRIPTOR);
				String _arg0;
				_arg0 = data.readString();
				this.setDeviceStub(_arg0);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_BtDialStub: {
				data.enforceInterface(DESCRIPTOR);
				String _arg0;
				_arg0 = data.readString();
				this.BtDialStub(_arg0);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_BtDtmfStub: {
				data.enforceInterface(DESCRIPTOR);
				byte _arg0;
				_arg0 = data.readByte();
				this.BtDtmfStub(_arg0);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_InCallStub: {
				data.enforceInterface(DESCRIPTOR);
				boolean _result = this.InCallStub();
				reply.writeNoException();
				reply.writeInt(((_result) ? (1) : (0)));
				return true;
			}
			case TRANSACTION_PbapConnectedStub: {
				data.enforceInterface(DESCRIPTOR);
				boolean _result = this.PbapConnectedStub();
				reply.writeNoException();
				reply.writeInt(((_result) ? (1) : (0)));
				return true;
			}
			}
			return super.onTransact(code, data, reply, flags);
		}

		private static class Proxy implements
				PbapService {
			private android.os.IBinder mRemote;

			Proxy(android.os.IBinder remote) {
				mRemote = remote;
			}

			@Override
			public android.os.IBinder asBinder() {
				return mRemote;
			}

			public String getInterfaceDescriptor() {
				return DESCRIPTOR;
			}

			@Override
			public void PullphonebookStub() throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_PullphonebookStub, _data,
							_reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void PullOutCallsStub() throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_PullOutCallsStub, _data,
							_reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void PullInCallsStub() throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_PullInCallsStub, _data,
							_reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void setDeviceStub(String address)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(address);
					mRemote.transact(Stub.TRANSACTION_setDeviceStub, _data,
							_reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void BtDialStub(String number)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(number);
					mRemote.transact(Stub.TRANSACTION_BtDialStub, _data,
							_reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void BtDtmfStub(byte code) throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeByte(code);
					mRemote.transact(Stub.TRANSACTION_BtDtmfStub, _data,
							_reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public boolean InCallStub() throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				boolean _result;
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_InCallStub, _data,
							_reply, 0);
					_reply.readException();
					_result = (0 != _reply.readInt());
				} finally {
					_reply.recycle();
					_data.recycle();
				}
				return _result;
			}

			@Override
			public boolean PbapConnectedStub()
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				boolean _result;
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_PbapConnectedStub, _data,
							_reply, 0);
					_reply.readException();
					_result = (0 != _reply.readInt());
				} finally {
					_reply.recycle();
					_data.recycle();
				}
				return _result;
			}
		}

		static final int TRANSACTION_PullphonebookStub = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
		static final int TRANSACTION_PullOutCallsStub = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
		static final int TRANSACTION_PullInCallsStub = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
		static final int TRANSACTION_setDeviceStub = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
		static final int TRANSACTION_BtDialStub = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
		static final int TRANSACTION_BtDtmfStub = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
		static final int TRANSACTION_InCallStub = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
		static final int TRANSACTION_PbapConnectedStub = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
	}

	public void PullphonebookStub() throws android.os.RemoteException;

	public void PullOutCallsStub() throws android.os.RemoteException;

	public void PullInCallsStub() throws android.os.RemoteException;

	public void setDeviceStub(String address)
			throws android.os.RemoteException;

	public void BtDialStub(String number)
			throws android.os.RemoteException;

	public void BtDtmfStub(byte code) throws android.os.RemoteException;

	public boolean InCallStub() throws android.os.RemoteException;

	public boolean PbapConnectedStub() throws android.os.RemoteException;
}
