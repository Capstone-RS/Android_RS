package com.cookandroid.capstone;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cookandroid.capstone.Fragment.HomeFragment;
import com.cookandroid.capstone.Fragment.ProfileFragment;
import com.google.android.gms.auth.api.Auth;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    private SignInButton btn_google; // 구글 로그인 버튼
    private FirebaseAuth auth; // 파이어 베이스 인증 객체
    private static final int RC_SIGN_IN = 9001; // 구글 로그인 결과 코드
    private GoogleSignInClient googleSignInClient; // 구글 API 클라이언트 객체
    //private static final int REQ_SIGN_GOOGLE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 앱이 실행될때 처음 수행되는 곳
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin); // signin xml이랑 연결된 자바파일이라는 뜻
        btn_google = findViewById(R.id.btn_google);

        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //구글 인증후 결과 받아냄
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
//            if(task.isSuccessful()){
//                GoogleSignInAccount account=task.getResult();//account에는 구글 로그인 정보를 담고 있다.(닉네임, 프로필사진, 이멜주소등)
//                resultLogin(account);//로그인 결과 값 출력 수행하라는 메소드
//            }
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("nickName", user.getDisplayName());
            intent.putExtra("eMail", user.getEmail());
            intent.putExtra("photoUrl", String.valueOf(user.getPhotoUrl())); // String.valueOf() 특정 자료형을 String 형태로 변환.

            startActivity(intent);
            finish();
        }
    }


//    private void resultLogin(GoogleSignInAccount account) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()) { // 로그인이 성공했으면
//                            Toast.makeText(SignInActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//
////                            intent = new Intent(getApplicationContext(), ProfileFragment.class);
//                            intent.putExtra("nickName", account.getDisplayName());
//                            intent.putExtra("eMail", account.getEmail());
//                            intent.putExtra("photoUrl", String.valueOf(account.getPhotoUrl())); // String.valueOf() 특정 자료형을 String 형태로 변환.
//                            startActivity(intent);
//                            finish(); // SignActivity 종료하여 다시 뒤로 돌아갈 수 없게 함.
//                        } else { // 로그인이 실패했으면
//                            Toast.makeText(SignInActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

}