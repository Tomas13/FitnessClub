package kz.grandprixclub.grandprixclubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import kz.grandprixclub.grandprixclubapp.Fragments.FragmentCalendar;
import kz.grandprixclub.grandprixclubapp.Fragments.FragmentCard;
import kz.grandprixclub.grandprixclubapp.Fragments.FragmentDetox;
import kz.grandprixclub.grandprixclubapp.Fragments.FragmentDiary;
import kz.grandprixclub.grandprixclubapp.Fragments.FragmentPersonal;
import kz.grandprixclub.grandprixclubapp.Fragments.FragmentRecords;
import kz.grandprixclub.grandprixclubapp.Fragments.FragmentRecordsNew;
import kz.grandprixclub.grandprixclubapp.Fragments.FragmentServices;
import kz.grandprixclub.grandprixclubapp.Fragments.FragmentShares;
import kz.grandprixclub.grandprixclubapp.Models.Diary.DiaryCalendar;
import kz.grandprixclub.grandprixclubapp.Models.User;
import kz.grandprixclub.grandprixclubapp.ServerRequests.DiaryCalendarHistoryRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.UserRequest;
import kz.grandprixclub.grandprixclubapp.Utils.RoundImage;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;

    TextView tvUserName, tvUserLastName, tvUserPhone;
    ImageView ivAvatar;
    RoundImage roundImage;
    FragmentPersonal fragmentPersonal;
    FragmentServices fragmentServices;
    FragmentRecords fragmentRecords;
    FragmentRecordsNew fragmentRecordsNew;
    FragmentDetox fragmentDetox;
    FragmentCard fragmentCard;
    FragmentCalendar fragmentCalendar;
    FragmentDiary fragmentDiary;
    FragmentShares fragmentShares;
    CaldroidFragment mCaldroidFragment;

    Boolean hideMenuItemRight;
    int navigationMenuItemID;

    DiaryCalendar[][] diaryCalendars;

    private Calendar myCalendar = Calendar.getInstance();

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String CALDROID_TAG = "CALDROID_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //View navHead = navigationView.inflateHeaderView(R.layout.nav_header_main);
        View navHead = navigationView.getHeaderView(0);

        tvUserName = (TextView) navHead.findViewById(R.id.tvUserName);
        tvUserLastName = (TextView) navHead.findViewById(R.id.tvUserLastName);
        tvUserPhone = (TextView) navHead.findViewById(R.id.tvUserPhone);
        ivAvatar = (ImageView) navHead.findViewById(R.id.imageView);

        this.getUserInfo();

        fragmentServices = new FragmentServices();
        fragmentPersonal = new FragmentPersonal();
        fragmentRecords = new FragmentRecords();
        fragmentRecordsNew = new FragmentRecordsNew();
        fragmentDetox = new FragmentDetox();
        fragmentCard = new FragmentCard();
        fragmentCalendar = new FragmentCalendar();
        fragmentDiary = new FragmentDiary();
        fragmentShares = new FragmentShares();
        sharesFragmentShow();



    }

    private void sharesFragmentShow() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_main, fragmentShares).commit();
        getSupportActionBar().setTitle(getString(R.string.shares_item));
        navigationView.getMenu().getItem(1).setChecked(true);
        hideMenuItemRight = true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isAuthenticated()) {
            MainActivity.this.finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

    }

    private boolean isAuthenticated () {
        if (User.isAuthenticated(MainActivity.this)) {
            return true;
        }
        return false;
    }

    public void getUserInfo() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result")
                            .getBoolean("has_error");
                    if (!has_error) {
                        JSONObject jUser = jsonResponse.getJSONObject("result")
                                .getJSONObject("user");
                        User user = new User(jUser);
                        //Log.d("ASD", user.getName());
                        tvUserName.setText(user.getName());
                        tvUserLastName.setText(user.getLastname());
                        tvUserPhone.setText(user.getPhone());
                        ImageRequest ir = new ImageRequest(user.getAvatar(), new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                roundImage = new RoundImage(response);
                                ivAvatar.setImageDrawable(roundImage);
                            }
                        }, 0, 0, null, null);
                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                        queue.add(ir);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };

        Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = "";
                if (error instanceof NetworkError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                } else if (error instanceof ServerError) {
                    message = "Сервер не найден. Пожалуйста, повторите попытку позже!";
                } else if (error instanceof AuthFailureError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                } else if (error instanceof ParseError) {
                    message = "Пожалуйста, повторите попытку позже!";
                } else if (error instanceof NoConnectionError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                } else if (error instanceof TimeoutError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                }

                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        UserRequest userRequest = new UserRequest(User.getSavedApiToken(MainActivity.this), responseListener, responseErrorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(userRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        CaldroidFragment caldroidFragment = (CaldroidFragment) getSupportFragmentManager().findFragmentByTag(CALDROID_TAG);
        if (caldroidFragment != null && caldroidFragment.isVisible()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_main, fragmentDiary).commit();
            getSupportActionBar().setTitle(getString(R.string.diary_item));
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed();
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItemRight = menu.findItem(R.id.action_item);
        if (hideMenuItemRight) {
            menuItemRight.setVisible(false);
        } else {
            if (navigationMenuItemID == R.id.nav_personal) {
                menuItemRight.setIcon(R.drawable.ic_mode_edit_white_24dp);
            } else if (navigationMenuItemID == R.id.nav_diary) {
                menuItemRight.setIcon(R.drawable.ic_history_white_24dp);
            }
            menuItemRight.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item) {
            if (navigationMenuItemID == R.id.nav_diary) {
                /*DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, onDateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();*/

                getDiaryCalendarHistory(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH) + 1, false);

            } else if (navigationMenuItemID == R.id.nav_personal) {
                Log.d(TAG, "EDIT PROFILE");
                startActivityForResult(new Intent(MainActivity.this, ProfileEditActivity.class), 1);
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private CaldroidFragment setCaldroidFragmentData(CaldroidFragment cFragment, Boolean isUpdate) {
        HashMap<Date, Drawable> map = new HashMap<>();
        HashMap<Date, Integer> map2 = new HashMap<>();
        for (int i = 0; i < diaryCalendars.length; i++) {
            DiaryCalendar[] dCalendars = diaryCalendars[i];
            for (int j = 0; j < 7; j++) {
                DiaryCalendar diaryCalendar = dCalendars[j];

                if (diaryCalendar.getHasHistory() != null && diaryCalendar.getHasHistory()) {
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(diaryCalendar.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    map.put(date, new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
                    map2.put(date, android.R.color.white);
                }
            }
        }

        mCaldroidFragment = cFragment;
        if (isUpdate) {
            mCaldroidFragment.setCalendarDate(myCalendar.getTime());
        } else {
            Bundle args = new Bundle();
            args.putInt(CaldroidFragment.MONTH, myCalendar.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, myCalendar.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, false);
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
            args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, false);
            args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CaldroidGrandPrix);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);
            mCaldroidFragment.setArguments(args);
        }

        if (map.size() > 0) {
            mCaldroidFragment.setBackgroundDrawableForDates(map);
            mCaldroidFragment.setTextColorForDates(map2);
        }

        return mCaldroidFragment;
    }

    private void showDiaryHistoryCalendar() {
        //hideMenuItemRight = true;

        CaldroidFragment caldroidFragment = setCaldroidFragmentData(new CaldroidFragment(), false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_main, caldroidFragment, CALDROID_TAG);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(R.string.diary_history_item);
        final CaldroidListener caldroidListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                if (calendar.get(Calendar.MONTH) != myCalendar.get(Calendar.MONTH)) {
                    return;
                }
                int weekNumber;
                int dayNumber;
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    weekNumber = calendar.get(Calendar.WEEK_OF_MONTH) - 1; // calendar's weeks begins from sunday
                    if (date.getDay() == 0) {
                        dayNumber = 7;
                    } else {
                        dayNumber = date.getDay(); //not happens
                    }
                } else {
                    weekNumber = calendar.get(Calendar.WEEK_OF_MONTH);
                    dayNumber = date.getDay();
                }
                /*Log.d(TAG, String.format(
                        "Date %s, year %s, month %s, day %s, day2 %s, week %s",
                        calendar.get(Calendar.DATE),
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        dayNumber,
                        calendar.get(Calendar.DAY_OF_WEEK),
                        weekNumber)
                );

                Log.d(TAG, diaryCalendars[weekNumber - 1][dayNumber - 1].getDate());*/
                if (!diaryCalendars[weekNumber - 1][dayNumber - 1].getHasHistory()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, DiaryDateHistoryActivity.class);
                intent.putExtra("date", diaryCalendars[weekNumber - 1][dayNumber - 1].getDate());
                startActivity(intent);
            }

            @Override
            public void onChangeMonth(int month, int year) {
                //mCaldroidFragment.clearSelectedDates();
                if (myCalendar.get(Calendar.MONTH) + 1 != month) {
                    myCalendar.set(Calendar.MONTH, month - 1);
                    myCalendar.set(Calendar.YEAR, year);
                    getDiaryCalendarHistory(year, month, true);
                }
                Log.d(TAG, "month: " + month + ", year: " + year);
            }
        };
        caldroidFragment.setCaldroidListener(caldroidListener);
    }

    private void getDiaryCalendarHistory(int year, int month, final boolean isUpdate) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean has_error = jsonResponse.getJSONObject("result")
                            .getBoolean("has_error");
                    if (!has_error) {
                        JSONObject jCal = jsonResponse.getJSONObject("result").getJSONObject("calendar");
                        if (jCal.length() > 0) {
                            diaryCalendars = DiaryCalendar.getDiaryCalendarsAsArray(jCal);
                            if (!isUpdate) {
                                showDiaryHistoryCalendar();
                            } else {
                                mCaldroidFragment.clearSelectedDates();
                                CaldroidFragment caldroidFragment = setCaldroidFragmentData(mCaldroidFragment, true);
                                caldroidFragment.refreshView();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };

        Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = "";
                if (error instanceof NetworkError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                } else if (error instanceof ServerError) {
                    message = "Сервер не найден. Пожалуйста, повторите попытку позже!";
                } else if (error instanceof AuthFailureError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                } else if (error instanceof ParseError) {
                    message = "Пожалуйста, повторите попытку позже!";
                } else if (error instanceof NoConnectionError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                } else if (error instanceof TimeoutError) {
                    message = "Отсутствует интернет-соединение. Проверьте подключение!";
                }

                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Пожалуйста подождите");
        progressDialog.show();
        DiaryCalendarHistoryRequest diaryCalendarHistoryRequest = new DiaryCalendarHistoryRequest(User.getSavedApiToken(MainActivity.this),
                year, month, responseListener, responseErrorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(diaryCalendarHistoryRequest);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        navigationMenuItemID = item.getItemId();

        if (navigationMenuItemID == R.id.nav_personal || navigationMenuItemID == R.id.nav_diary) {
            hideMenuItemRight = false;
        } else {
            hideMenuItemRight = true;
        }
        invalidateOptionsMenu();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (navigationMenuItemID) {
            case R.id.nav_personal:
                fragmentTransaction.replace(R.id.content_main, fragmentPersonal).commit();
                getSupportActionBar().setTitle(getString(R.string.personal_item));
                break;
            case R.id.nav_my_calendar:
                fragmentTransaction.replace(R.id.content_main, fragmentCalendar).commit();
                getSupportActionBar().setTitle(getString(R.string.calendar_item));
                break;
            case R.id.nav_invitation:
                fragmentTransaction.replace(R.id.content_main, fragmentRecordsNew).commit();
                getSupportActionBar().setTitle(getString(R.string.invitation_item));
                break;
            case R.id.nav_diary:
                fragmentTransaction.replace(R.id.content_main, fragmentDiary).commit();
                getSupportActionBar().setTitle(getString(R.string.diary_item));
                break;
            case R.id.nav_medical:
                fragmentTransaction.replace(R.id.content_main, fragmentCard).commit();
                getSupportActionBar().setTitle(getString(R.string.medical_item));

                break;
            case R.id.nav_services:
                fragmentTransaction.replace(R.id.content_main, fragmentServices).commit();
                getSupportActionBar().setTitle(getString(R.string.services_item));
                break;
            case R.id.nav_detox:
                fragmentTransaction.replace(R.id.content_main, fragmentDetox).commit();
                getSupportActionBar().setTitle(getString(R.string.detox_item));
                break;
            case R.id.nav_shares:
                fragmentTransaction.replace(R.id.content_main, fragmentShares).commit();
                getSupportActionBar().setTitle(getString(R.string.shares_item));
                break;
            case R.id.nav_exit:
                User.userLogoutAction(MainActivity.this);
                MainActivity.this.finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Boolean isUpdateNeeded = data.getBooleanExtra("is_update_needed", true);
        String action = data.getStringExtra("action");

        if (isUpdateNeeded) {
            if (action != null && action.length() > 0) {
                if (action.equals("records")) {
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("update_user_records"));
                }
            } else {
                getUserInfo();
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("update_profile"));
            }
        }
    }
}
