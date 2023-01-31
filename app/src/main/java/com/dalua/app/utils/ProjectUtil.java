package com.dalua.app.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.navigation.Navigation;

import com.dalua.app.R;
import com.dalua.app.models.GeoLocationResponse;
import com.dalua.app.models.LoginResponse;
import com.dalua.app.models.ResponseOtaFiles;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
//    implementation 'com.google.code.gson:gson:2.8.6'

public class ProjectUtil {

    private static final String APP_NAME = "DaluaApplication";
    private static final String TAG = "ProjectUtil";

    private ProjectUtil() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
    }

    public static String getUserToken(Context context) {
        return getSharedPreferences(context).getString("user_token", null);
    }

    public static ResponseOtaFiles getOtaFiles(Context context) {
        return stringToObject(getSharedPreferences(context).getString("ota_files", null), ResponseOtaFiles.class);
    }

    public static void saveOtaFiles(Context context, ResponseOtaFiles responseOtaFiles) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(responseOtaFiles);
        Log.d(TAG, "saveOtaFiles: " + json);
        editor.putString("ota_files", json);
        editor.apply();
    }

    public static void saveOtaFile1(Context context, ResponseOtaFiles.OtaFile otaFile) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(otaFile);
        editor.putString("ota_file1", json);
        editor.apply();
    }

    public static void saveOtaFile2(Context context, ResponseOtaFiles.OtaFile otaFile) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(otaFile);
        editor.putString("ota_file2", json);
        editor.apply();
    }

    public static void putUserToken(Context context, String value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("user_token", value);
        editor.apply();
    }

    public static String getAquariumClicked(Context context) {
        return getSharedPreferences(context).getString("user_aquarium", null);
    }

    public static void putAquariumClicked(Context context, String value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("user_aquarium", value);
        editor.apply();
    }

//    public static String getTokenCode(Context context) {
//        return getSharedPreferences(context).getString("token_code", null);
//    }

    public static String toSignificantFiguresString(BigDecimal bd, int significantFigures) {
        String test = String.format("%." + significantFigures + "G", bd);
        if (test.contains("E+")) {
            test = String.format(Locale.US, "%.0f", Double.valueOf(String.format("%." + significantFigures + "G", bd)));
        }
        return test;
    }

//    public static void putTokenCode(Context context, String value) {
//        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
//        editor.putString("token_code", value);
//        editor.apply();
//    }

    public static String getApiTokenBearer(Context context) {
        return getSharedPreferences(context).getString("token_beareer", null);
    }

    public static void putApiTokenBearer(Context context, String value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("token_beareer", value);
        editor.apply();
    }


//    public static void putUUID(Context context, String value) {
//        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
//        editor.putString("uuid", value);
//        editor.apply();
//    }
//
//    public static String getUUID(Context context) {
//        return getSharedPreferences(context).getString("uuid", "");
//    }

    public static boolean getBoolean(Context context) {
        return getSharedPreferences(context).getBoolean("islogin", false);
    }

    public static void putBoolean(Context context, boolean value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("islogin", value);
        editor.apply();
    }

    public static boolean getWalkingThrough(Context context) {
        return getSharedPreferences(context).getBoolean("is_ist_time", true);
    }

    public static void putWalkingThrough(Context context, boolean value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("is_ist_time", value);
        editor.apply();
    }

    public static String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public static String convertTime24to12(String time_in_24hrFormate) {

        String[] split = time_in_24hrFormate.split(":");
        return String.format(Locale.getDefault(), "%02d", Integer.parseInt(split[0]) % 12) + ":" + (split[1]) + " " + ((Integer.parseInt(split[0]) >= 12) ? "PM" : "AM");

    }

    public static int getInt(Context context, String key, int defValue) {
        return getSharedPreferences(context).getInt(key, defValue);
    }

    public static void putInt(Context context, String key, int value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getCrollerValues(Context context, String defValue) {
        return getSharedPreferences(context).getString("keydfdfdf", defValue);
    }

    public static void putCrollerValues(Context context, String value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("keydfdfdf", value);
        editor.apply();
    }

//    public static <T> T getObject(Context context, String key, Class<T> objectClass) {
//        Gson gson = new Gson();
//        String json = getSharedPreferences(context).getString(key, null);
//        Object object = null;
//        if (json != null) {
//            object = gson.fromJson(json, (Type) objectClass);
//        }
//        return Primitives.wrap(objectClass).cast(object);
//    }
//
//    public static void putObject(Context context, String key, Object value) {
//        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(value);
//        editor.putString(key, json);
//        editor.apply();
//    }

    private static String ChangeSecToTimeFormate(int total_sec) {

        int hours = (int) Math.floor(total_sec / 3600);
        total_sec %= 3600;
        int minutes = (int) Math.floor(total_sec / 60);
        int seconds = total_sec % 60;
        String khan = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        return khan;
    }


    public static void transparentStatusBar(Activity activity) {

        //set this in your parent theme
        //        <item name="android:statusBarColor">@color/transparent</item>
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }


    public static boolean validatePasswrod(String password) {

        String COMPLEX_PASSWORD_REGEX =
                "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|" +
                        "(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|" +
                        "(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|" +
                        "(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})" +
                        "[A-Za-z0-9!~<>,;:_=?*+#.\"&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]" +
                        "{8,32}$";

        Pattern PASSWORD_PATTERN = Pattern.compile(COMPLEX_PASSWORD_REGEX);

        return PASSWORD_PATTERN.matcher(password).matches();

    }

    public static GeoLocationResponse getLocationObjects(Context context) {

        Gson gson = new Gson();
        String json = getSharedPreferences(context).getString("dfdsdfsdfs", null);
        GeoLocationResponse object = null;
        if (json != null) {
            object = gson.fromJson(json, GeoLocationResponse.class);
        }
        return object;

    }

    public static void putLocationObjects(Context context, GeoLocationResponse value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString("dfdsdfsdfs", json);
        editor.apply();
    }


    public static LoginResponse getUserObjects(Context context) {
        Gson gson = new Gson();
        String json = getSharedPreferences(context).getString("usersaf", null);
        LoginResponse object = null;
        if (json != null) {
            object = gson.fromJson(json, LoginResponse.class);
        }
        return object;

    }

    public static void putUserObjects(Context context, LoginResponse value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString("usersaf", json);
        editor.apply();
    }

    public static boolean deleteCasheDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteCasheDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static void hideKeyBoard(Activity activity, EditText value) {

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(value.getWindowToken(), 0);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public static void gotoNextFragment(Activity activity, int action_id) {
        Navigation.findNavController(activity, R.id.nav_host_fragment_container).navigate(action_id);
    }


//    public static void loadFragment(FragmentManager fm, Fragment fragment) {
//        fm.beginTransaction().replace(R.id.container, fragment, fragment.getClass().getSimpleName())
//                .commit();
//    }
//
//    public static void loadFragment(FragmentManager fm, Fragment fragment, String backStack) {
//        fm.beginTransaction().replace(R.id.container, fragment, fragment.getClass().getSimpleName())
//                .addToBackStack(backStack).commit();
//    }

//    public static void setStatusBarGradient(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            Drawable background = activity.getResources().getDrawable(R.drawable.drawable_status_bar);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
//        }
//    }
//
//    public static void setStatusBarGradientHomeFragment(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            Drawable background = activity.getResources().getDrawable(R.drawable.drawable_status_bar_hmfrag);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
//        }
//    }
//
//
//    public static void setStatusBarPRofileScreenBgcolorless(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            Drawable background = activity.getResources().getDrawable(R.drawable.drawable_status_bar_colorless);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
//        }
//    }
//
//    public static void setStatusBarPRofileScreenBg(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            Drawable background = activity.getResources().getDrawable(R.drawable.drawable_status_bar_scrnbg);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
//        }
//    }
//
//    public static void setStatusBarGradientContactFrag(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            Drawable background = activity.getResources().getDrawable(R.drawable.drawable_status_bar_blue);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
//        }
//    }

    public static ProgressDialog makeProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");
        return progressDialog;
    }


    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    public static boolean IsConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }


    public static String gettimeinStringFromate() {
        return String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());
    }

    public static String getTimeinAMorPM(Integer hourOfDay) {
        return (hourOfDay < 12) ? "AM" : "PM";
    }

    public static String getTimestampBackConversion(String message_time) {

        Date date = new Date(Long.parseLong(message_time));
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT+5"));
        String formatted = format.format(date);
        return formatted;

    }

//    public static void bundle() {
//        Fragment fragment = new Fragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt(key, value);
//        fragment.setArguments(bundle);
//    }

//    public static void getbundle() {
//        Bundle bundle = this.getArguments();
//        if (bundle != null) {
//            int myInt = bundle.getInt(key, defaultValue);
//        }
//    }

    public static String getErrorMessage(String responseobj) {

        try {
            JSONObject jObjError = new JSONObject(responseobj);
            Object errordata = jObjError.get("message");
            if (errordata instanceof JSONArray) {
                // It's an array
                JSONArray jsonarray = jObjError.getJSONArray("message");

                if (jsonarray.length() == 1) {
                    return jsonarray.getString(0);
                } else if (jsonarray.length() == 2) {
                    return jsonarray.getString(0) + "\n" + jsonarray.getString(1);
                } else if (jsonarray.length() == 3) {
                    return jsonarray.getString(0) + "\n" + jsonarray.getString(1) + "\n" + jsonarray.getString(2);
                } else if (jsonarray.length() == 4) {
                    return jsonarray.getString(0) + "\n" + jsonarray.getString(1) + "\n" +
                            jsonarray.getString(2) + "\n" + jsonarray.getString(3);
                } else if (jsonarray.length() == 5) {
                    return jsonarray.getString(0) + "\n" + jsonarray.getString(1) + "\n" +
                            jsonarray.getString(2) + "\n" + jsonarray.getString(3) + "\n" + jsonarray.getString(4);
                }

            } else if (errordata instanceof String) {
                // It's an object
                return errordata.toString();
            }

        } catch (Exception e) {
            Log.d(TAG, "getErrorMessage: " + e.getLocalizedMessage());
        }
        return "Sorry... Try again.";
    }

    public static String objectToString(Object value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);
        return json;
    }

    public static <T> T stringToObject(String classs_obj, Class<T> objectClass) {
        return Primitives.wrap(objectClass).cast(new Gson().fromJson(classs_obj, (Type) objectClass));
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "dalua");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create Dir");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_ " + timeStamp + ".jpg");
        String TempImg = mediaFile.toString();
        return mediaFile;

    }

    public static int getStatusBarHeight(Activity activity) {
        Rect rectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return contentViewTop - statusBarHeight;
    }

    public static void showToastMessage(Activity activity, boolean success, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//        LayoutInflater inflater = activity.getLayoutInflater();
//        View layout;
//        if (!success)
//            layout = inflater.inflate(R.layout.item_toast_falure_layout, (ViewGroup) activity.findViewById(R.id.toast_root));
//        else
//            layout = inflater.inflate(R.layout.item_toast_success_layout, (ViewGroup) activity.findViewById(R.id.toast_root));
//
//        TextView text = (TextView) layout.findViewById(R.id.toast_tv);
//        text.setText(message);
//
//        Toast toast = new Toast(activity.getApplicationContext());
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 20);
//        toast.setView(layout);
//        toast.show();
    }

    public static void startRevealActivity(View v, Intent intent) {

        //calculates the center of the View v you are passing
        int revealX = (int) (v.getX() + v.getWidth() / 2);
        int revealY = (int) (v.getY() + v.getHeight() / 2);

        //create an intent, that launches the second activity and pass the x and y coordinates
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        //just start the activity as an shared transition, but set the options bundle to null
        ActivityCompat.startActivity(v.getContext(), intent, null);

        //to prevent strange behaviours override the pending transitions
        ((Activity) v.getContext()).overridePendingTransition(0, 0);

    }

    public static void startRightLeftActivity(View v, Intent intent) {

        //calculates the center of the View v you are passing
//        int revealX = (int) (v.getX() + v.getWidth() / 2);
//        int revealY = (int) (v.getY() + v.getHeight() / 2);

//        //create an intent, that launches the second activity and pass the x and y coordinates
//        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, revealX);
//        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        //just start the activity as an shared transition, but set the options bundle to null
        ActivityCompat.startActivity(v.getContext(), intent, null);
        //to prevent strange behaviours override the pending transitions
        ((Activity) v.getContext()).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public static void startRevealActivityNewTask(View v, Intent intent) {

        //calculates the center of the View v you are passing
        int revealX = (int) (v.getX() + v.getWidth() / 2);
        int revealY = (int) (v.getY() + v.getHeight() / 2);

        //create an intent, that launches the second activity and pass the x and y coordinates
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, revealY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //just start the activity as an shared transition, but set the options bundle to null
        ActivityCompat.startActivity(v.getContext(), intent, null);

        //to prevent strange behaviours override the pending transitions
        ((Activity) v.getContext()).overridePendingTransition(0, 0);

    }

    // save list to shared preferences


    // This four methods are used for maintaining favorites.
    public static void saveDevices(Context context, List<String> uid) {

        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(uid);
        editor.putString("list_o", jsonFavorites);
        editor.apply();

    }

    public static void addConnectedDevice(Context context, String uid) {
        List<String> favorites = getAllConnectedDevices(context);
        if (favorites == null)
            favorites = new ArrayList<>();
        favorites.add(uid);
        saveDevices(context, favorites);
    }

    public static void removeConnectedDevice(Context context, String product) {
        List<String> favorites = getAllConnectedDevices(context);
        if (favorites != null) {
            favorites.remove(product);
            Log.d(TAG, "removeConnectedDevice: ");
            saveDevices(context, favorites);
        }
    }

    public static void removeAllConnectedDevices(Context context) {
        List<String> favorites = getAllConnectedDevices(context);
        if (favorites != null) {
            favorites.clear();
            Log.d(TAG, "removeConnectedDevice: ");
            saveDevices(context, null);
        }
    }

    public static List<String> getAllConnectedDevices(Context context) {

        List<String> favorites;
        if (getSharedPreferences(context).contains("list_o")) {
            String jsonFavorites = getSharedPreferences(context).getString("list_o", null);
            Gson gson = new Gson();
            String[] favoriteItems = gson.fromJson(jsonFavorites,
                    String[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else
            return null;

        return favorites;
    }


    public static boolean toolTip(Context context, boolean show) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("showTootTip", show);
        editor.apply();
        return false;
    }

    public static boolean isShowToolTip(Context context) {
        return getSharedPreferences(context).getBoolean("showTootTip", true);
    }

}




