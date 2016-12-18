package kz.grandprixclub.grandprixclubapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.jar.Manifest;

import kz.grandprixclub.grandprixclubapp.Models.User;
import kz.grandprixclub.grandprixclubapp.ServerRequests.UserEditRequest;
import kz.grandprixclub.grandprixclubapp.ServerRequests.UserRequest;
import kz.grandprixclub.grandprixclubapp.Utils.RoundImage;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfileEditActivity extends AppCompatActivity {

    private static final String TAG = ProfileEditActivity.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private User user;
    ImageView ivAvatar;
    RoundImage roundImage;
    EditText etUserLastName;
    EditText etUserName;
    EditText etUserBirthday;
    EditText etUserAddress;
    EditText etUserNewPassword;
    EditText etUserNewPasswordConfirm;
    Button bSaveProfile;
    private String selectedBirthday;

    Bitmap mBitmap;

    private Calendar myCalendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateEditTextBirthday();
        }
    };

    private void updateEditTextBirthday() {
        String myFormat = "dd.MM.yyyy";
        String serverFormat = "yyyy-MM-dd";
        Date birthday = myCalendar.getTime();

        etUserBirthday.setText(new SimpleDateFormat(serverFormat, Locale.US).format(birthday));
        selectedBirthday = (String) new SimpleDateFormat(serverFormat, Locale.US).format(birthday);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Профиль");

        ivAvatar = (ImageView) findViewById(R.id.imageView);
        etUserLastName = (EditText) findViewById(R.id.etUserLastName);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etUserBirthday = (EditText) findViewById(R.id.etUserBirthday);
        etUserAddress = (EditText) findViewById(R.id.etUserAddress);
        etUserNewPassword = (EditText) findViewById(R.id.etUserNewPassword);
        etUserNewPasswordConfirm = (EditText) findViewById(R.id.etUserNewPasswordConfirm);
        bSaveProfile = (Button) findViewById(R.id.bSaveProfile);

        etUserBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileEditActivity.this, onDateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        bSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(ProfileEditActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                }
                EasyImage.openChooserWithGallery(ProfileEditActivity.this, "Выберите фото", 0);
            }
        });

        EasyImage.configuration(this)
                .setImagesFolderName("GrandPrixClub")
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true);

        getUserInfo();
    }

    @Override
    protected void onDestroy() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
        super.onDestroy();
    }

    public void getUserInfo() {
        final ProgressDialog progressDialog = new ProgressDialog(ProfileEditActivity.this);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (ProfileEditActivity.this != null) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                        if (!has_error) {
                            JSONObject jUser = jsonResponse.getJSONObject("result").getJSONObject("user");
                            user = new User(jUser);

                            ImageRequest ir = new ImageRequest(user.getAvatar(), new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    roundImage = new RoundImage(response);
                                    ivAvatar.setImageDrawable(roundImage);
                                }
                            }, 0, 0, null, null);
                            mRequestQueue.add(ir);
                            etUserLastName.setText(user.getLastname());
                            etUserName.setText(user.getName());
                            etUserAddress.setText(user.getAddress());
                            etUserBirthday.setText(user.getBirthday());
                            selectedBirthday = user.getBirthday();


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

                if (ProfileEditActivity.this != null) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileEditActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        };
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Загрузка");
        progressDialog.show();
        UserRequest userRequest = new UserRequest(User.getSavedApiToken(ProfileEditActivity.this), responseListener, responseErrorListener);
        mRequestQueue = Volley.newRequestQueue(ProfileEditActivity.this);
        mRequestQueue.add(userRequest);
    }

    private void saveUserProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(ProfileEditActivity.this);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (ProfileEditActivity.this != null) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean has_error = jsonResponse.getJSONObject("result").getBoolean("has_error");
                        if (!has_error) {
                            setResult(RESULT_OK, new Intent().putExtra("is_update_needed", true));
                            finish();
                        } else {
                            Log.d(TAG, response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

                if (ProfileEditActivity.this != null) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileEditActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        };

        if (etUserNewPassword.getText().length() > 0) {
            if (!checkPasswordAndConfirmPassword()) {
                Toast.makeText(ProfileEditActivity.this, "Поле подтверждение пароля не совпадает с паролем!", Toast.LENGTH_LONG).show();
            } else {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Сохранение");
                progressDialog.show();
                UserEditRequest userEditRequest = new UserEditRequest(User.getSavedApiToken(ProfileEditActivity.this), etUserName.getText().toString(),
                        etUserLastName.getText().toString(), selectedBirthday, etUserAddress.getText().toString(), mBitmap,
                        etUserNewPassword.getText().toString(), etUserNewPasswordConfirm.getText().toString(), responseListener, responseErrorListener);
                mRequestQueue = Volley.newRequestQueue(ProfileEditActivity.this);
                mRequestQueue.add(userEditRequest);
            }

        } else {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Сохранение");
            progressDialog.show();
            UserEditRequest userEditRequest = new UserEditRequest(User.getSavedApiToken(ProfileEditActivity.this), etUserName.getText().toString(),
                    etUserLastName.getText().toString(), selectedBirthday, etUserAddress.getText().toString(), mBitmap, responseListener, responseErrorListener);
            mRequestQueue = Volley.newRequestQueue(ProfileEditActivity.this);
            mRequestQueue.add(userEditRequest);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                mBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                /*roundImage = new RoundImage(mBitmap);
                ivAvatar.setImageDrawable(roundImage);*/
                ivAvatar.setImageBitmap(mBitmap);
            }

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                super.onImagePickerError(e, source, type);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                super.onCanceled(source, type);

                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(ProfileEditActivity.this);
                    if (photoFile != null) {
                        photoFile.delete();
                    }
                }
            }
        });
    }

    private boolean checkPasswordAndConfirmPassword() {
        boolean pstatus = false;
        if (etUserNewPassword.getText().length() > 0) {
            String password = etUserNewPassword.getText().toString();
            String confirmPassword = etUserNewPasswordConfirm.getText().toString();
            if (confirmPassword != null && password != null)
            {
                if (password.equals(confirmPassword))
                {
                    pstatus = true;
                }
            }
        }
        return pstatus;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
