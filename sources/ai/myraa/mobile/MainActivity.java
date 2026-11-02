package ai.myraa.mobile;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

/* JADX INFO: loaded from: classes6.dex */
public class MainActivity extends BridgeActivity {
    @Override // com.getcapacitor.BridgeActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(MyraaNativePlugin.class);
        super.onCreate(savedInstanceState);
    }
}
