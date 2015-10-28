package com.dudu.android.launcher.utils;

/**<p>经纬度坐标转换工具</p>
 * 1真实坐标转换火星坐标(四维/高德/图盟/易图通适用)<br/>
 * 2火星坐标转换真实坐标(四维/高德/图盟/易图通适用)<br/>
 * 3真实坐标转换百度坐标(百度适用)<br/>
 * 4百度坐标转换真实坐标(百度适用)<br/>
 * @author 2013
 *
 */
public class Coordinate {
	/**大陆包括海南岛的多边形区域， 不包含台湾*/
	private static double[][] china=new double[][]{
		{97.737445951504114,28.410153499111768},{98.726156281455602,27.136738266074641},
		{98.611085620367035,26.001781208457405},{97.861275369390285,25.310677509705883},
		{97.506791627283079,24.545922279115164},{97.528840786238163,23.829167019538357},
		{98.605618926093086,24.143067860485569},{98.748160317297874,23.676931063446489},
		{98.956875261018055,23.250016295695016},{99.461679756275444,21.747572076518907},
		{101.85648361433394,21.093143019609546},{101.69150811948003,22.328423233211851},
		{102.60364169145028,22.704060437587923},{103.81141524348364,22.358927882426041},
		{105.5240474369394,23.088401333653472},{106.6553064575039,22.886226577341048},
		{106.61697435472284,22.094501181637032},{107.4566644684334,21.543209682166463},
		{108.01709005468381,21.522809055769304},{108.11599767587219,19.028119584701241},
		{109.08263687180576,17.735090223985956},{112.02604612934439,18.23691828362773},
		{112.72914675948959,20.928834697827668},{113.19309159747228,21.696486555410413},
		{113.56687374299146,22.038424128725111},{114.00907759985544,22.505059868285201},
		{114.11251504033662,22.528233526212777},{114.15992130355023,22.549781616979192},
		{114.23213040290148,22.551435305267169},{114.32747827529788,22.553825141926868},
		{114.5083047307056,22.396291248516601},{114.8672339627492,22.43768067263694},
		{115.6492451061652,22.620298056161563},{116.15178805891404,22.739125474407736},
		{119.54131397124829,24.335763033323232},{121.16748539773488,27.419511931070399},
		{121.38705572273588,27.692126069174179},{121.43104743039228,27.614303387648857},
		{123.14459616401749,30.19087609627692},{120.41964885742928,34.891687255772574},
		{122.79287646859346,36.567404059616621},{124.4179127352737,39.473513623291609},
		{127.14231088164776,41.64234165146317},{128.63659766661559,41.379433059209063},
		{132.06285645758075,43.550309058101874},{134.61177105234248,47.19170907441876},
		{135.44555534335566,48.430911181697709},{131.1829521221245,47.844427376839022},
		{125.33798441773408,53.253805455422793},{121.95547711254108,53.359132068344927},
		{119.84551330151812,53.069987392524581},{116.59412377335336,49.499160293545792},
		{115.31933248264748,47.489687626190232},{119.14279611550585,47.13213874218696},
		{111.49631574955836,44.87432236955317},{110.7941590194668,42.97513699549323},
		{104.90852140368868,41.774398047849651},{96.7815273338,42.975463451188006},
		{91.067105603253495,45.739980810762752},{91.056088971942984,47.088075956580731},
		{89.39595212822077,48.110367555205322},{88.21502505611447,48.588921376159142},
		{87.822182002933062,49.201108494595616},{86.836594531852171,49.14909610638081},
		{85.765326582386393,48.406993498789738},{85.240788396878244,47.090270427620823},
		{82.980008723644417,47.215471104308051},{82.079010701476221,45.4904338376383},
		{79.937875907071117,45.015225950529242},{80.294799429622543,42.806837880676333},
		{78.916324261935969,41.610950359374989},{73.883624911453737,40.032363067072822},
		{73.531776790860789,38.725001008524707},{74.872636039482444,38.467508373385776},
		{74.454922366039554,37.095450504170707},{76.102729132220091,35.876968420135498},
		{77.949246027764403,35.234109597565926},{78.850515558506231,34.323511869656215},
		{78.476755947613242,32.628799436824139},{78.58694907277102,31.286770290357747},
		{81.156965521681855,29.963294349410621},{81.937111072683066,30.343550599886456},
		{82.958778407811906,29.562978338294357},{85.364843454045243,28.18765908815395},
		{86.15882556230676,27.913734175386441},{87.128028032781089,27.833538884297457},
		{88.691235726495094,28.015783820725105},{89.067251555941752,27.34174929121669},
		{89.699362402418288,28.14421734635788},{90.947144371123258,27.935776367095102},
		{91.644856418288285,27.561382976554611},{92.144675429662684,26.774392843240371},
		{93.803634112633844,26.999897477202264},{95.671698798661353,28.168411882245078},
		{97.017868361559977,27.71685865366976},{97.69354725151436,28.400536995883115}};
	/**澳门区域*/
	private static double[][] aomen=new double[][]{{113.53942,22.208786},{113.560074,22.219627},
		{113.60612,22.126143},{113.558014,22.09939},{113.547,22.107643},{113.540115,22.167442},
		{113.52496,22.182673},{113.53047,22.189049}};
	/**判断点在多边形内外
	 * @param p 点坐标{x,y}
	 * @param b 多边形的点数组,多组{x,y}
	 * @return 多边形内返回true,否则返回false
	 */
	private static boolean isPolygon(double [] p,double[][] b){
	    boolean  flag=false;
	    int size=b.length;
	    if(size<3)return flag;
	    double[] dp=null;double[] dt=null;
	    for(int i=0;i<size;i++){
	    	dp=b[i];
	    	dt=b[(i+1)%size];
	        if(p[1]<dp[1]&&p[1]<dt[1])continue;
	        if(dp[0]<=p[0]&&dt[0]<=p[0])continue;
	        double dx=dt[0]-dp[0];
	        double dy=dt[1]-dp[1]; 
	        double t= (p[0]-dp[0])/dx;//求得交点的t值
	        double y=t*dy + dp[1];
	        if(y<=p[1]&&t>=0&&t<=1)flag=!flag;
	    }
	    dp=null;dt=null;
	    return flag;
	}
	/**判断位置点是不是需要纠偏
	 * @param p 位置点
	 * @return 需要纠偏返回true, 否则返回false 
	 */
	private static boolean isInChina(double []p){
		boolean isInChina=false;
		isInChina =isPolygon(p,china);
		if(isInChina){//在中国，此时要排除澳门
			if(isPolygon(p,aomen)){
				isInChina=false;
			}
		}
		return isInChina;
	}
     private static double yj_sin2(double x) {
         double tt;
         double ss;
         int ff;
         double s2;
         int cc;
         ff = 0;
         if (x < 0) {
            x = -x;
            ff = 1;
         }
         cc = (int) (x / 6.28318530717959);
         tt = x - cc * 6.28318530717959;
         if (tt > 3.1415926535897932) {
             tt = tt - 3.1415926535897932;
             if (ff == 1)ff = 0;
             else if (ff == 0)ff = 1;
         }
         x = tt;
         ss = x;
         s2 = x;
         tt = tt * tt;
         s2 = s2 * tt;
         ss = ss - s2 * 0.166666666666667;
         s2 = s2 * tt;
         ss = ss + s2 * 8.33333333333333E-03;
         s2 = s2 * tt;
         ss = ss - s2 * 1.98412698412698E-04;
         s2 = s2 * tt;
         ss = ss + s2 * 2.75573192239859E-06;
         s2 = s2 * tt;
         ss = ss - s2 * 2.50521083854417E-08;
         if (ff == 1)ss = -ss;
         return ss;
     }
     private static double Transform_yj5(double x, double y) {
    	 double tt;
         tt = 300 + 1 * x + 2 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.sqrt(x * x));
         tt = tt + (20 * yj_sin2(18.849555921538764 * x) + 20 * yj_sin2(6.283185307179588 * x)) * 0.6667;
         tt = tt + (20 * yj_sin2(3.141592653589794 * x) + 40 * yj_sin2(1.047197551196598 * x)) * 0.6667;
         tt = tt + (150 * yj_sin2(0.2617993877991495 * x) + 300 * yj_sin2(0.1047197551196598 * x)) * 0.6667;
         return tt;
     }
	 private static double Transform_yjy5(double x, double y) {
		 double tt;
         tt = -100 + 2 * x + 3 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.sqrt(x * x));
         tt = tt + (20 * yj_sin2(18.849555921538764 * x) + 20 * yj_sin2(6.283185307179588 * x)) * 0.6667;
         tt = tt + (20 * yj_sin2(3.141592653589794 * y) + 40 * yj_sin2(1.047197551196598 * y)) * 0.6667;
         tt = tt + (160 * yj_sin2(0.2617993877991495 * y) + 320 * yj_sin2(0.1047197551196598 * y)) * 0.6667;
         return tt;
	 }
	 private static double Transform_jy5(double x, double xx) {
         double n;
         double a;
         double e;
         a = 6378245;
         e = 0.00669342;
         n = Math.sqrt(1 - e * yj_sin2(x * 0.0174532925199433) * yj_sin2(x * 0.0174532925199433));
         n = (xx * 180) / (a / n * Math.cos(x * 0.0174532925199433) * 3.1415926);
         return n;
	 }
	 private static double Transform_jyj5(double x, double yy) {
         double m;
         double a;
         double e;
         double mm;
         a = 6378245;
         e = 0.00669342;
         mm = 1 - e * yj_sin2(x * 0.0174532925199433) * yj_sin2(x * 0.0174532925199433);
         m = (a * (1 - e)) / (mm * Math.sqrt(mm));
         return (yy * 180) / (m * 3.1415926);
	 }
     private static double random_yj(double casm_rr) {
         int t;
         int casm_a;
         int casm_c;
         casm_a = 314159269;
         casm_c = 453806245;
         casm_rr = casm_a * casm_rr + casm_c;
         t = (int) (casm_rr / 2);
         casm_rr = casm_rr - t * 2;
         casm_rr = casm_rr / 2;
         return (casm_rr);
	 }
     /**真实坐标转火星坐标
     * @param wg_flag 0不转换，1转换
     * @param wg_lng  经度
     * @param wg_lat  纬度
     * @param wg_heit 海拔
     * @param wg_week
     * @param wg_time
     * @return
     */
    public static double[] wgtochina_lb(int wg_flag, double wg_lng, double wg_lat, int wg_heit, int wg_week, long wg_time) {
    	 double[] db = new double[]{wg_lng,wg_lat};
         boolean flag=isInChina(db);
    	 if(!flag){//不需要纠偏
    		 return db;
    	 }
    	 double x_add;
         double y_add;
         double h_add;
         double x_l;
         double y_l;
         double casm_v;
         double t1_t2;
         double x1_x2;
         double y1_y2;
         if(wg_heit > 5000) {
              return db;
         }
         x_l = wg_lng;
         y_l = wg_lat;
         if(x_l < 72.004||x_l > 137.8347) {
             return db;
         }
         if(y_l < 0.8293||y_l > 55.8271) {
             return db;
         }
         if(wg_flag==0) {
             //IniCasm(wg_time, wg_lng, wg_lat);
             db=new double[]{wg_lng,wg_lat};
             return db;
         }

         double casm_rr=0.0;
         double casm_x1=0.0;
         double casm_y1=0.0;
         double casm_x2=0.0;
         double casm_y2=0.0;
         double casm_f=0.0;
         long casm_t1=0;
         long casm_t2=0;
         
         casm_t2=wg_time;
         t1_t2 = (double) (casm_t2 - casm_t1) / 1000.0;
         if(t1_t2 <= 0) {
             casm_t1 = casm_t2;
             casm_f = casm_f + 1;
             casm_x1 = casm_x2;
             casm_f = casm_f + 1;
             casm_y1 = casm_y2;
             casm_f = casm_f + 1;
         }else{
             if(t1_t2 > 120) {
                 if (casm_f == 3) {
                         casm_f = 0;
                         casm_x2 = wg_lng;
                         casm_y2 = wg_lat;
                         x1_x2 = casm_x2 - casm_x1;
                         y1_y2 = casm_y2 - casm_y1;
                         casm_v = Math.sqrt(x1_x2 * x1_x2 + y1_y2 * y1_y2) / t1_t2;
                         if (casm_v > 3185) {
                         	return (db);
                         }

                 }
                 casm_t1 = casm_t2;
                 casm_f = casm_f + 1;
                 casm_x1 = casm_x2;
                 casm_f = casm_f + 1;
                 casm_y1 = casm_y2;
                 casm_f = casm_f + 1;
             }
         }
         x_add = Transform_yj5(x_l - 105, y_l - 35);
         y_add = Transform_yjy5(x_l - 105, y_l - 35);
         h_add = wg_heit;
         
         casm_rr=random_yj(casm_rr);
         x_add = x_add + h_add * 0.001 + yj_sin2(wg_time * 0.0174532925199433) + casm_rr;
         casm_rr=random_yj(casm_rr);
         y_add = y_add + h_add * 0.001 + yj_sin2(wg_time * 0.0174532925199433) + casm_rr;

         db=new double[]{(x_l + Transform_jy5(y_l, x_add)),(y_l + Transform_jyj5(y_l, y_add))};
         db[0]=(db[0]*1000000)/1000000d;db[1]=(db[1]*1000000)/1000000d;
         return db;
     }
	/**火星坐标转真实坐标
	* @param gg_lng 经度
	* @param gg_lat 纬度
	* @return 返回[经度,纬度]
	*/
    public static double[] chinatowg(double gg_lon, double gg_lat){
  	   double[] db=wgtochina_lb(1,gg_lon,gg_lat,0,0,0);
  	   if(db==null)return db;
	   double wg_lat = 2*gg_lat-db[1];
       double wg_lon = 2*gg_lon-db[0];
       db=new double[]{wg_lon,wg_lat};
       db[0]=(db[0]*1000000)/1000000d;db[1]=(db[1]*1000000)/1000000d;
	   return db;
    }
    /**真实坐标转火星坐标
    * @param gg_lng 经度
    * @param gg_lat 纬度
    * @return 返回[经度,纬度]
    */
   public static double[] wgtochina(double wg_lon, double wg_lat){
   	 	double[] db=wgtochina_lb(1,wg_lon,wg_lat,0,0,0);
   	 	return db;
    }
     /**真实坐标转百度坐标
     * @param bd_lat 经度
     * @param wg_lat 纬度
     * @return 返回[经度,纬度]
     */
     public static double[] wgtobaidu(double wg_lon, double wg_lat){
    	 double[] db=wgtochina_lb(1,wg_lon,wg_lat,0,0,0);
    	 if(db==null)return db;
    	 db=chinatobaidu(db[0],db[1]);
    	 return db;
     }
 	/**百度坐标转真实坐标
 	* @param bd_lon 经度
 	* @param bd_lat 纬度
 	* @return 返回[经度,纬度]
 	*/
     public static double[] baidutowg(double bd_lon, double bd_lat){
        double[] db=baidutochina(bd_lon,bd_lat);
   	    if(db==null)return db;
   	    double gg_lon=db[0],gg_lat=db[1];
   	    db=wgtochina(gg_lon,gg_lat);
   	    if(db==null)return db;
   	    double wg_lat = 2*gg_lat-db[1];
        double wg_lon = 2*gg_lon-db[0];
        db=new double[]{wg_lon,wg_lat};
        db[0]=(db[0]*1000000)/1000000d;db[1]=(db[1]*1000000)/1000000d;
 	   	return db;
     }
    static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
     /**火星坐标转百度坐标
     * @param gg_lon 经度
     * @param gg_lat 纬度
     * @return 返回[经度,纬度]
     */
    public static double[] chinatobaidu(double gg_lon, double gg_lat){
         double x = gg_lon, y = gg_lat;  
         double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);  
         double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
         double bd_lon = z * Math.cos(theta) + 0.0065;  
         double bd_lat = z * Math.sin(theta) + 0.006;  
         double[] db=new double[]{bd_lon,bd_lat};
         db[0]=(db[0]*1000000)/1000000d;db[1]=(db[1]*1000000)/1000000d;
    	 return db;
     }
    /**百度坐标转火星坐标
    * @param bd_lon 经度
    * @param bd_lat 纬度
    * @return 返回[经度,纬度]
    */
     public static double[] baidutochina(double bd_lon,double bd_lat)  {  
         double x = bd_lon - 0.0065, y = bd_lat - 0.006;  
         double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);  
         double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);  
         double gg_lon = z * Math.cos(theta); 
         double gg_lat = z * Math.sin(theta);   
         double[] db=new double[]{gg_lon,gg_lat};
         db[0]=(db[0]*1000000)/1000000d;db[1]=(db[1]*1000000)/1000000d;
    	 return db;
     }
     public static void main(String[] str){
    	 double wg_lon=114.52807188034058,wg_lat=38.00538637435228;
    	 /*真实114.52807188034058,38.00538637435228
    	  *http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=114.52807188034058&y=38.00538637435228
    	  *百度114.54058563361,38.011649880129
    	  *http://api.map.baidu.com/ag/coord/convert?from=0&to=2&x=114.52807188034058&y=38.00538637435228
    	  *谷歌114.53399576823,38.005982801649
    	  */
    	 //double wg_lon=116.39129519462585,wg_lat=39.90728376400555;
    	 //double wg_lon=116.39105916023254,wg_lat=39.912534248044494;
    	 double[] db=wgtochina(wg_lon, wg_lat);
    	 if(db!=null){
    		 System.out.println("真实坐标="+wg_lon+","+wg_lat+"\r\n火星坐标="+db[0]+","+db[1]);
    		 double lon=db[0],lat=db[1];
    		 db=chinatowg(lon,lat);
    		 if(db!=null)System.out.println("火星坐标="+lon+","+lat+"\r\n真实坐标="+db[0]+","+db[1]);
    	 }
    	 System.out.println();
    	 db=wgtobaidu(wg_lon, wg_lat);
    	 if(db!=null){
    		 System.out.println("真实坐标="+wg_lon+","+wg_lat+"\r\n百度坐标="+db[0]+","+db[1]);
    		 double lon=db[0],lat=db[1];
    		 db=baidutowg(lon,lat);
    		 if(db!=null)System.out.println("百度坐标="+lon+","+lat+"\r\n真实坐标="+db[0]+","+db[1]);
    	 }
     }
}
