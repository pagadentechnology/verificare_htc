package id.tech.htctools;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import id.tech.adapters.Rest_Adapter;
import id.tech.htctools.R;
import id.tech.models.PojoResponseRowCount;
import id.tech.util.CustomAdapter_Img;
import id.tech.util.GPSTracker;
import id.tech.util.Parameter_Collections;
import id.tech.util.Public_Functions;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Part;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

public class UpdateBranding_Activity extends ActionBarActivity {
    Button btn;
    String mUrl_Img_00, mUrl_Img_01, mUrl_Img_02, mUrl_Img_03;
    CustomAdapter_Img adapter;
    ImageView imgview_00, imgview_01, imgview_02, imgview_03;
    public static int CODE_UPLOAD = 111;
    HorizontalScrollView horizontalScroll;
    EditText ed, ed_namatoko, ed_ket;
    SharedPreferences spf;
    private String id_pegawai;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_updatebranding);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update Branding");

        spf = getSharedPreferences(Parameter_Collections.SH_NAME, Context.MODE_PRIVATE);
//		kode_toko = spf.getString(Parameter_Collections.SH_KODE_TOKO, "");
        id_pegawai = spf.getString(Parameter_Collections.SH_ID_PEGAWAI, "");

        mUrl_Img_00 = null;
        mUrl_Img_01 = null;
        mUrl_Img_02 = null;
        mUrl_Img_03 = null;

        getAllView();
    }

    private void getAllView() {
        horizontalScroll = (HorizontalScrollView) findViewById(R.id.wrapper_horizontalview);
        imgview_00 = (ImageView) findViewById(R.id.img_00);
        imgview_01 = (ImageView) findViewById(R.id.img_01);
        imgview_02 = (ImageView) findViewById(R.id.img_02);
        imgview_03 = (ImageView) findViewById(R.id.img_03);

        ed = (EditText) findViewById(R.id.ed);
        ed_namatoko = (EditText) findViewById(R.id.ed_namatoko);
        ed_ket = (EditText) findViewById(R.id.ed_ket);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent upload = new Intent(getApplicationContext(),
                        UploadImageDialog.class);
                startActivityForResult(upload, CODE_UPLOAD);
                // adapter = new CustomAdapter_Img(getApplicationContext(), 0,
                // 0, data);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.updatebrand, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                Public_Functions.delete_IssuePhoto();
                Toast.makeText(getApplicationContext(), "Canceled. Images deleted", Toast.LENGTH_LONG).show();
                finish();
                break;

            case R.id.action_send_updatebranding:

                GPSTracker gps = new GPSTracker(getApplicationContext());
                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    spf.edit().putString(Parameter_Collections.TAG_LONGITUDE_NOW, String.valueOf(longitude)).commit();
                    spf.edit().putString(Parameter_Collections.TAG_LATITUDE_NOW, String.valueOf(latitude)).commit();


                    if (Public_Functions.isNetworkAvailable(getApplicationContext())) {
//				boolean b = true;
//				if (b) {
                        new Async_SubmitUpdateBranding_Retrofit().execute();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "No Internet Connection, Cek Your Network",
                                Toast.LENGTH_LONG).show();
                    }


                } else {

                    if (Public_Functions.isNetworkAvailable(getApplicationContext())) {
                        new Async_SubmitUpdateBranding().execute();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "No Internet Connection, Cek Your Network",
                                Toast.LENGTH_LONG).show();
                    }

                    Toast.makeText(
                            getApplicationContext(),
                            "Can not get your location now, Sent your last locations",
                            Toast.LENGTH_LONG).show();
                }

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == RESULT_OK) {

            if (requestCode == CODE_UPLOAD) {

                if (mUrl_Img_00 == null) {
                    horizontalScroll.setVisibility(View.VISIBLE);

                    mUrl_Img_00 = data.getStringExtra("mUrl_Img");
                    Bitmap b = BitmapFactory.decodeFile(mUrl_Img_00);
                    imgview_00.setVisibility(View.VISIBLE);
                    imgview_00.setImageBitmap(b);
                } else if (mUrl_Img_01 == null) {
                    mUrl_Img_01 = data.getStringExtra("mUrl_Img");
                    Bitmap b = BitmapFactory.decodeFile(mUrl_Img_01);
                    imgview_01.setVisibility(View.VISIBLE);
                    imgview_01.setImageBitmap(b);
                } else if (mUrl_Img_02 == null) {
                    mUrl_Img_02 = data.getStringExtra("mUrl_Img");
                    Bitmap b = BitmapFactory.decodeFile(mUrl_Img_02);
                    imgview_02.setVisibility(View.VISIBLE);
                    imgview_02.setImageBitmap(b);
                } else if (mUrl_Img_03 == null) {
                    mUrl_Img_03 = data.getStringExtra("mUrl_Img");
                    Bitmap b = BitmapFactory.decodeFile(mUrl_Img_03);
                    imgview_03.setVisibility(View.VISIBLE);
                    imgview_03.setImageBitmap(b);
                }

            }

        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Public_Functions.delete_IssuePhoto();
        Toast.makeText(getApplicationContext(), "Canceled. Images deleted", Toast.LENGTH_LONG).show();
        finish();
    }

    private class Async_SubmitUpdateBranding_Retrofit extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdialog;
        String respondMessage;
        DialogFragmentProgress dialogProgress;
        String cDesc, cNamaToko, cKet, row_count,cMessage;
        String url_file00, url_file01, url_file02, url_file03;
        File sourceFile00, sourceFile01, sourceFile02, sourceFile03;
        RequestBody body00,body01,body02,body03;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogProgress = new DialogFragmentProgress();
            dialogProgress.show(getSupportFragmentManager(), "");

            cDesc = ed.getText().toString();
            cNamaToko = ed_namatoko.getText().toString();
            cKet = ed_ket.getText().toString();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(mUrl_Img_00 != null){
                sourceFile00 = new File(mUrl_Img_00);
                body00 = RequestBody.create(MediaType.parse("image/*"), sourceFile00);
            }
            if(mUrl_Img_01 != null){
                sourceFile01 = new File(mUrl_Img_01);
                body01 = RequestBody.create(MediaType.parse("image/*"), sourceFile01);
            }
            if(mUrl_Img_02 != null){
                sourceFile02 = new File(mUrl_Img_02);
                body02 = RequestBody.create(MediaType.parse("image/*"), sourceFile02);
            }if(mUrl_Img_03 != null){
                sourceFile03 = new File(mUrl_Img_03);
                body03 = RequestBody.create(MediaType.parse("image/*"), sourceFile03);
            }

            RequestBody cKindnya = RequestBody.create(MediaType.parse("text/plain"), Parameter_Collections.KIND_UPDATE_BRAND);
            RequestBody cNamaTokonya = RequestBody.create(MediaType.parse("text/plain"), cNamaToko);
            RequestBody cIdPegawainya = RequestBody.create(MediaType.parse("text/plain"), id_pegawai);
            RequestBody cLatnya = RequestBody.create(MediaType.parse("text/plain"), spf.getString(Parameter_Collections.TAG_LATITUDE_NOW, "0"));
            RequestBody cLonginya = RequestBody.create(MediaType.parse("text/plain"),  spf.getString(Parameter_Collections.TAG_LONGITUDE_NOW, "0"));
            RequestBody cKetnya = RequestBody.create(MediaType.parse("text/plain"), cKet);
            RequestBody cPesannya = RequestBody.create(MediaType.parse("text/plain"), cDesc);

            Retrofit retrofit = new Retrofit.Builder().baseUrl(Parameter_Collections.URL_BASE).addConverterFactory(GsonConverterFactory.create())
                    .build();
            Rest_Adapter adapter = retrofit.create(Rest_Adapter.class);
            Call<PojoResponseRowCount> call = adapter.input_update_branding(cKindnya,cNamaTokonya,cIdPegawainya,
                    cLatnya,cLonginya, cKetnya, cPesannya, body00, body01,body02,body03);

            try{
                Response<PojoResponseRowCount> response = call.execute();
                if (response.isSuccess()) {
                    if (response.body().getJsonCode() == 1) {
                        row_count = "1";
                    } else {
                        row_count= "0";
                        cMessage = response.errorBody().toString();
                    }
                } else {
                    row_count = "0";
                    cMessage = response.errorBody().toString();
                }
            }catch (IOException e){

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if (row_count.equals("1")) {
                dialogProgress.dismiss();

                DialogLocationConfirmation dialog = new DialogLocationConfirmation(
                        getApplicationContext(), "Update Branding Success", 9);
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "");
//				Toast.makeText(getApplicationContext(), "Update Branding Success", Toast.LENGTH_LONG).show();
//				finish();
            } else {
                dialogProgress.dismiss();

                DialogLocationConfirmation dialog = new DialogLocationConfirmation(
                        getApplicationContext(), cMessage, 9);
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "");
//				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//				finish();
            }
        }
    }

    private class Async_SubmitUpdateBranding extends AsyncTask<Void, Void, String> {
        ProgressDialog pdialog;
        String respondMessage;
        JSONObject jsonResult;
        DialogFragmentProgress dialogProgress;
        String cDesc, cNamaToko, cKet;
        int serverRespondCode = 0;

        String url_file00, url_file01, url_file02, url_file03;
        File sourceFile00, sourceFile01, sourceFile02, sourceFile03;
        FileInputStream fileInputStream00, fileInputStream01, fileInputStream02, fileInputStream03;
        private String row_count;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialogProgress = new DialogFragmentProgress();
            dialogProgress.show(getSupportFragmentManager(), "");

            cDesc = ed.getText().toString();
            cNamaToko = ed_namatoko.getText().toString();
            cKet = ed_ket.getText().toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            return uploadDataForm(mUrl_Img_00, mUrl_Img_01, mUrl_Img_02, mUrl_Img_03);

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            JSONObject jObj = jsonResult;
            try {
                row_count = jObj.getString(Parameter_Collections.TAG_ROWCOUNT);

            } catch (JSONException e) {
                row_count = "0";

            }

            if (row_count.equals("1")) {
                dialogProgress.dismiss();

                DialogLocationConfirmation dialog = new DialogLocationConfirmation(
                        getApplicationContext(), "Update Branding Success", 9);
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "");
//				Toast.makeText(getApplicationContext(), "Update Branding Success", Toast.LENGTH_LONG).show();			
//				finish();
            } else {
                dialogProgress.dismiss();

                DialogLocationConfirmation dialog = new DialogLocationConfirmation(
                        getApplicationContext(), result, 9);
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "");
//				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();			
//				finish();
            }


        }

        private String uploadDataForm(String url_gambar00, String url_gambar01,
                                      String url_gambar02, String url_gambar03) {
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;


            if (url_gambar00 != null) {
                url_file00 = url_gambar00;
                sourceFile00 = new File(url_file00);
            }
            if (url_gambar01 != null) {
                url_file01 = url_gambar01;
                sourceFile01 = new File(url_file01);
            }
            if (url_gambar02 != null) {
                url_file02 = url_gambar02;
                sourceFile02 = new File(url_file02);
            }
            if (url_gambar03 != null) {
                url_file03 = url_gambar03;
                sourceFile03 = new File(url_file03);
            }

            try {
                URL url = new URL(Parameter_Collections.URL_INSERT);

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                if (url_gambar00 != null) {
                    conn.setRequestProperty("img0", url_file00);
                }
                if (url_gambar01 != null) {
                    conn.setRequestProperty("img1", url_file01);
                }
                if (url_gambar02 != null) {
                    conn.setRequestProperty("img2", url_file02);
                }
                if (url_gambar03 != null) {
                    conn.setRequestProperty("img3", url_file03);
                }


                dos = new DataOutputStream(conn.getOutputStream());

                if (url_gambar00 != null) {
                    fileInputStream00 = new FileInputStream(
                            sourceFile00);
                    //img 00
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"img0\";filename=\""
                            + url_file00 + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream00.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream00.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream00.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream00.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                }
                if (url_gambar01 != null) {
                    fileInputStream01 = new FileInputStream(
                            sourceFile01);
                    //img 01
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"img1\";filename=\""
                            + url_file01 + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream01.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream01.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream01.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream01.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                }
                if (url_gambar02 != null) {
                    fileInputStream02 = new FileInputStream(
                            sourceFile02);
                    //img 02
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"img2\";filename=\""
                            + url_file02 + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream02.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream02.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream02.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream02.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                }
                if (url_gambar03 != null) {
                    fileInputStream03 = new FileInputStream(
                            sourceFile03);
                    //img 03
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"img3\";filename=\""
                            + url_file03 + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream03.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream03.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream03.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream03.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                }


                // param kind
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\""
                        + Parameter_Collections.KIND + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(Parameter_Collections.KIND_UPDATE_BRAND + lineEnd);


                // param kode toko
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\""
                        + Parameter_Collections.TAG_NAMA_TOKO + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(cNamaToko + lineEnd);

                Log.e("Kode Toko", cNamaToko);

                // param id pegawai
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\""
                        + Parameter_Collections.TAG_ID_PEGAWAI + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(id_pegawai + lineEnd);

                // param latitude
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\""
                        + Parameter_Collections.TAG_LAT_UPDATE_BRANDING + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(spf.getString(Parameter_Collections.TAG_LATITUDE_NOW, "") + lineEnd);

                // param longitude
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\""
                        + Parameter_Collections.TAG_LONG_UPDATE_BRANDING + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(spf.getString(Parameter_Collections.TAG_LONGITUDE_NOW, "") + lineEnd);

                // param keterangan
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\""
                        + Parameter_Collections.TAG_KET_UPDATE_BRAND + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(cKet + lineEnd);


                // param desc program
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\""
                        + Parameter_Collections.TAG_UPDATE_BRAND + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(cDesc + lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                serverRespondCode = conn.getResponseCode();
                respondMessage = conn.getResponseMessage();

                Log.e("RESPOND", respondMessage);

                if (serverRespondCode == 200) {
                    Log.e("CODE ", "Success Upload");
                } else {
                    Log.e("CODE ", "Success failed");
                }


                if (url_gambar00 != null) {
                    fileInputStream00.close();
                }
                if (url_gambar01 != null) {
                    fileInputStream01.close();
                }
                if (url_gambar02 != null) {
                    fileInputStream02.close();
                }
                if (url_gambar03 != null) {
                    fileInputStream03.close();
                }


                dos.flush();

                InputStream is = conn.getInputStream();
                int ch;

                StringBuffer buff = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    buff.append((char) ch);
                }
                Log.e("publish", buff.toString());

                jsonResult = new JSONObject(buff.toString());
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return respondMessage;
        }
    }

}
