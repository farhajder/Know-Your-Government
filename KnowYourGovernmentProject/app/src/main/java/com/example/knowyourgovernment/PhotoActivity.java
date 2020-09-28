package com.example.knowyourgovernment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {
    CivilGovernmentOfficial mainCivilGovernmentOfficial;
    ConstraintLayout backgroundLayoutConstraint;
    TextView aboutTxtView;
    TextView photoActBanText;
    ImageView officialImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        aboutTxtView = findViewById(R.id.paOfcialInfoText);
        officialImgView = findViewById(R.id.paImgView);
        backgroundLayoutConstraint = findViewById(R.id.phtActContraint);
        photoActBanText = findViewById(R.id.photoActBannerText);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Intent ofcActIntent = getIntent();
        CivilGovernmentOfficial civilGovernmentOfficial = (CivilGovernmentOfficial) ofcActIntent.getSerializableExtra(getString(R.string.SerializeGovementObject));
        mainCivilGovernmentOfficial = civilGovernmentOfficial;
        OfficialAddress locaAdd = civilGovernmentOfficial.getBannerTextAddress();
        setOfficialImage(civilGovernmentOfficial);
        aboutTxtView .setText(getOfficialInfoString(civilGovernmentOfficial));
        photoActBanText.setText(locaAdd.getCity()+","+locaAdd.getState()+","+locaAdd.getZip());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent clickedNoteIntent = new Intent();
        setResult(RESULT_OK, clickedNoteIntent);
        finish();
    }


    String getOfficialInfoString(CivilGovernmentOfficial civilGovermentOfficial){
        String aboutString = "";
        aboutString = civilGovermentOfficial.getOfficeName()
                + "\n"+ civilGovermentOfficial.getCivilOfficial().getOfficialName();
        if(civilGovermentOfficial.getCivilOfficial().getOfficialParty()!=null && civilGovermentOfficial.getCivilOfficial().getOfficialParty().length() > 0){
            if(civilGovermentOfficial.getCivilOfficial().getOfficialParty().equals(OfficialActivity.OfficialPartyConstant.PARTY_DEMOCRATIC)){
                int backgroundColor = getResources().getColor(R.color.colorBlue);
                backgroundLayoutConstraint.setBackgroundColor(backgroundColor);
            }
            else if (civilGovermentOfficial.getCivilOfficial().getOfficialParty().equals(OfficialActivity.OfficialPartyConstant.PARTY_REPUBLICAN)){
                int myColor = getResources().getColor(R.color.colorRed);
                backgroundLayoutConstraint.setBackgroundColor(myColor);
            }
        }
        return aboutString;
    }

    private void setOfficialImage(final CivilGovernmentOfficial civilGovermentOfficial){
        final String photoUrl = civilGovermentOfficial.getCivilOfficial().getOfficialPhotoLink();
        if ( photoUrl != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = photoUrl.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.hourglass)
                            .into(officialImgView);
                }
            }).build();
            picasso.load(photoUrl)
                    .fit()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.hourglass)
                    .into(officialImgView);
        } else {
            Picasso.get().load(photoUrl)
                    .fit()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.missing)
                    .into(officialImgView);
        }
    }
}
