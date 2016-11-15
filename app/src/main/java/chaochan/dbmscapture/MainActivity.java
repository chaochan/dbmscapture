package chaochan.dbmscapture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import chaochan.dbmscapture.model.SSAnalyzer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int GALLERY_IMAGE_REQUEST = 1;

    @BindView(R.id.image_name) ImageView mImageName;
    @BindView(R.id.image_sire_line) ImageView mImageSireLine;
    private SSAnalyzer.AnalyzedImage mAnalyzedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        mImageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    callCloudVision(mAnalyzedImage.nameArea);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mImageSireLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int width  = Math.max(mAnalyzedImage.nameArea.getWidth(), mAnalyzedImage.sireLineArea.getWidth());
                    int height = Math.max(mAnalyzedImage.nameArea.getHeight(), mAnalyzedImage.sireLineArea.getHeight());
                    final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    final Canvas canvas = new Canvas(bitmap);

                    canvas.drawBitmap(
                            mAnalyzedImage.nameArea,
                            new Rect(0, 0, mAnalyzedImage.nameArea.getWidth(), mAnalyzedImage.nameArea.getHeight()),
                            new Rect(0, 0, mAnalyzedImage.nameArea.getWidth(), mAnalyzedImage.nameArea.getHeight()),
                            new Paint());
                    canvas.drawBitmap(
                            mAnalyzedImage.sireLineArea,
                            new Rect(0, 0, mAnalyzedImage.sireLineArea.getWidth(), mAnalyzedImage.sireLineArea.getHeight()),
                            new Rect(0, mAnalyzedImage.nameArea.getHeight(), mAnalyzedImage.sireLineArea.getWidth(), mAnalyzedImage.nameArea.getHeight() + mAnalyzedImage.sireLineArea.getHeight()),
                            new Paint());

                    callCloudVision(bitmap);
                    //callCloudVision(mAnalyzedImage.sireLineArea);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 画像選択ボタン選択通知
     * @param sender
     */
    @OnClick(R.id.btn_select_image)
    protected void onClickSelectImage(View sender) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                GALLERY_IMAGE_REQUEST);
    }


    /**
     * 他Activityからの戻り
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            SSAnalyzer.AnalyzedImage image = SSAnalyzer.analyzeScreenShot(this, data.getData());
            mImageName.setImageBitmap(image.nameArea);
            mImageSireLine.setImageBitmap(image.sireLineArea);
            mAnalyzedImage = image;
        }
    }


    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        Toast.makeText(this, "アップロード開始", Toast.LENGTH_SHORT);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(BuildConfig.CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("TEXT_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        ImageContext imageContext = new ImageContext();
                        imageContext.setLanguageHints(Arrays.asList("ja"));
                        annotateImageRequest.setImageContext(imageContext);

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                Toast.makeText(MainActivity.this, "完了", Toast.LENGTH_SHORT).show();
                Log.d("debug", result);
            }
        }.execute();
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message += String.format("%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }

        return message;
    }
}
