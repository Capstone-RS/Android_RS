package com.cookandroid.capstone;


import static com.google.android.gms.auth.api.signin.GoogleSignIn.getClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private static final String TAG = SignInActivity.class.getSimpleName();

    private SignInButton btn_google; // 구글 로그인 버튼
    private LinearLayout btn_logout;
    private FirebaseAuth auth; // 파이어 베이스 인증 객체
    private static final int RC_SIGN_IN = 9001; // 구글 로그인 결과 코드
    public static Context context_main;
    private GoogleSignInAccount gsa; // 구글 계정
    private GoogleSignInClient googleSignInClient; // 구글 API 클라이언트 객체
    private GoogleSignInOptions googleSignInOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) { // 앱이 실행될때 처음 수행되는 곳

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin); // signin xml이랑 연결된 자바파일이라는 뜻

        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions); // 구글 API 클라이언트 객체

        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(view -> {
            // 기존 로그인 계정 확인
            if(gsa != null) // 로그인 되어있는 경우
                Toast.makeText(SignInActivity.this, "로그인 상태입니다.", Toast.LENGTH_SHORT).show();
            else
                signIn();
        });

        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            btn_google.setVisibility(View.GONE);
            updateUI(currentUser);
        }
    }

    private void signIn() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            updateProfile(currentUser);
            updateUI(currentUser);
        }else{
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        Log.i(TAG, "signIn => " + account);
//
//        if (account != null) {
//            firebaseAuthWithGoogle(account);
//        } else {
//            Intent signInIntent = googleSignInClient.getSignInIntent();
//            startActivityForResult(signInIntent, RC_SIGN_IN);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //구글 인증후 결과 받아냄
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.i(TAG, "onActivityResult " + task.isSuccessful() + " " + task.getException());
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e(TAG, "onActivityResult error " + e);
            }
        }

    }

    //    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = auth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            updateUI(null);
//                        }
//                    }
//                });
//    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Log.i(TAG, "signInWithCredential => " + task.isSuccessful() + " " + task.getException());
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = auth.getCurrentUser();
                        updateProfile(user);
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        updateUI(null);
                    }
                });
    }
    private void updateProfile(FirebaseUser user) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("nickName", user.getDisplayName());
        editor.putString("eMail", user.getEmail());
        editor.putString("photoUrl", String.valueOf(user.getPhotoUrl()));
        editor.apply();
    }
    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra("nickName", user.getDisplayName());
//            intent.putExtra("eMail", user.getEmail());
//            intent.putExtra("photoUrl", String.valueOf(user.getPhotoUrl())); // String.valueOf() 특정 자료형을 String 형태로 변환.
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