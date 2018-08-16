package as.mke.mail;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.io.*;

public class MainActivity extends Activity 
{
	TextView tv;
	ByteArrayOutputStream bao;
	Handler hd;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		tv=(TextView)findViewById(R.id.mainTextView1);
		
		bao=new ByteArrayOutputStream();
	
		
		final PrintStream ps=new PrintStream(bao);
		System.setOut(ps);
		System.setErr(ps);
		String[] s=new String[]{""};
		
		hd=new Handler(){
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				
				
				ps.flush();
				tv.setText(new String(bao.toByteArray()));
				
			}
		};
		
		new Thread(){
			public void run(){
		new Main().main(null);
		hd.sendEmptyMessage(0);
		}
		}.start();
		
		
		
    }
}
