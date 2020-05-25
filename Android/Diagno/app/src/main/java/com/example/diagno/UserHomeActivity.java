package com.example.diagno;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import java.util.Date;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Calendar;

public class UserHomeActivity extends BaseMenuActivity
{
    //constants
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "UserHomeActivity";
    private String deviceName = "Hc-05";
    private String deviceHardwareAddress = "FC:A8:9A:00:06:C2";
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID

    //layout elements
    private Button measureButton;
    private Button initButton;
    private Button quitButton;
    private Button diagButton;


    private TextView valVata;
    private TextView valPitta;
    private TextView valKapha;
    private TextView valTemp;


    //class variables
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;

    //ref
    ConnectThread mmConnectThread;
    ConnectedThread mmConnectedThread;

    //
    private String recv_str;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    static String[] s;



    private static class MyHandler extends Handler
    {
        //private String writeMessage;
        private final WeakReference<UserHomeActivity> mActivity;




        public MyHandler(UserHomeActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            UserHomeActivity activity = mActivity.get();
            if (activity != null) {
                // ...
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int)msg.arg1;
                int end = (int)msg.arg2;

                if (msg.what == 0) {
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);

                    activity.recv_str = writeMessage;
                    Log.i(TAG, "rcvd msg " + writeMessage);


                    s = activity.recv_str.split(",");
                    activity.valVata.setText(String.format("Vata: %s bpm", s[0]));
                    activity.valPitta.setText(String.format("Pitta: %s bpm", s[1]));
                    activity.valKapha.setText(String.format("Kapha: %s bpm", s[2]));
                    activity.valTemp.setText(String.format("Temperature: %s °F", s[3]));






                }
            }
        }

    }

    private final MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        setTitle("Home");

        measureButton = (findViewById(R.id.buttonMeasure));
        initButton = (findViewById(R.id.buttonInit));
        quitButton = (findViewById(R.id.buttonQuit));
        diagButton = findViewById(R.id.buttondiag);


        valVata = findViewById(R.id.valVata);
        valPitta = findViewById(R.id.valPitta);
        valKapha = findViewById(R.id.valKapha);
        valTemp = findViewById(R.id.valTemp);


        measureButton.setEnabled(false);
        initButton.setEnabled(false);
        quitButton.setEnabled(false);

        readDoc();
        /*valVata.setText(String.format("Vata: %s bpm", redret[0]));
        valPitta.setText(String.format("Pitta: %s bpm", redret[1]));
        valKapha.setText(String.format("Kapha: %s bpm", redret[2]));
        valTemp.setText(String.format("Temperature: %s °F", redret[3]));*/


        //initialise if bluetooth dev is found and paired
        if (initBluetooth())
        {
            initButton.setEnabled(true);
            Toast.makeText(UserHomeActivity.this,"Dev found and paired",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(UserHomeActivity.this,"Error in finding dev",Toast.LENGTH_LONG).show();
        }


        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                mmConnectThread = new ConnectThread(mmDevice);
                mmConnectThread.start();

                measureButton.setEnabled(true);
                quitButton.setEnabled(true);


            }
        });

        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                byte[] bytes = {'s'};
                mmConnectedThread.write(bytes);


            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                byte[] bytes = {'t'};
                mmConnectedThread.write(bytes);
                mmConnectedThread.cancel();
                measureButton.setEnabled(false);

                db.collection("Patients").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .update(
                                "Sensor data.Vata", s[0],
                                "Sensor data.Pitta", s[1],
                                "Sensor data.Kapha", s[2],
                                "Sensor data.Temp", s[3],
                                "EnquiryRaised", true


                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(UserHomeActivity.this, "Storage successful.",
                                //Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Stored");
                                Toast.makeText(UserHomeActivity.this,"Stored",Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(UserHomeActivity.this, "Storage failed",
                                //Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Could not store");
                            }
                        });
            }
        });

        diagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    readDiagno();

            }
        });
    }

    private boolean initBluetooth()
    {
        boolean found = false;

        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null)
        {
            Toast.makeText(UserHomeActivity.this,"Bluetooth isn't supported on your device",Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0)
        {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices)
            {
                if(deviceName.equals(device.getName()) ||
                        deviceHardwareAddress.equals(device.getAddress()))
                {
                    mmDevice = device;
                    found = true;
                    break;
                }

            }
        }
        else
        {
            Toast.makeText(UserHomeActivity.this,"Pair with your bluetooth device first",Toast.LENGTH_SHORT).show();
        }

        return found;
    }


    /**/
    private class ConnectThread extends Thread {

        //private boolean connectedflag = false;

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
                //Log.i(TAG, "Socket's create() method failed");
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            //manageMyConnectedSocket(mmSocket);
            //connectedflag = true;
            mmConnectedThread = new ConnectedThread(mmSocket);
            mmConnectedThread.start();



        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /**/
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        private boolean rflag = false;


        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
                Log.d(TAG, "creating input stream");
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
                Log.d(TAG, "creating output stream");
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes = 0; // bytes returned from read()
            int begin = 0;

            // Keep listening to the InputStream until # occurs.
            while (true) {
                try {
                    numBytes += mmInStream.read(mmBuffer, numBytes, mmBuffer.length - numBytes);
                    for(int i = begin; i < numBytes; i++) {
                        if(mmBuffer[i] == "#".getBytes()[0]) {
                            handler.obtainMessage(0, begin, i, mmBuffer).sendToTarget();
                            begin = i + 1;
                            if(i == numBytes - 1) {
                                numBytes = 0;
                                begin = 0;
                            }
                        }
                    }
                    Log.d(TAG, "msg "+numBytes);
                } catch (IOException e) {
                    break;
                }
            }

            rflag = true;
        }




        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MyBluetoothService.MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
                Log.d(TAG, "msg sent "+writtenMsg);
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MyBluetoothService.MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    public void readDiagno()
    {


        db.collection("Patients").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    //assert doc != null;
                    assert doc != null;
                    if (doc.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + doc.getString("Diagnosis"));

                        showAlertbox(doc.getString("Diagnosis"));
                    } else {
                        Log.d(TAG, "No such document");
                    }

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    public void readDoc()
    {

        final String[] retdoc = new String[4];

        db.collection("Patients").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    //assert doc != null;
                    assert doc != null;
                    if (doc.exists()) {
                        retdoc[0] = doc.get("Sensor data.Vata").toString();
                        retdoc[1] = doc.get("Sensor data.Pitta").toString();
                        retdoc[2] = doc.get("Sensor data.Kapha").toString();
                        retdoc[3] = doc.get("Sensor data.Temp").toString();

                        valVata.setText(String.format("Vata: %s bpm", retdoc[0]));
                        valPitta.setText(String.format("Pitta: %s bpm", retdoc[1]));
                        valKapha.setText(String.format("Kapha: %s bpm", retdoc[2]));
                        valTemp.setText(String.format("Temperature: %s °F", retdoc[3]));

                    } else {
                        Log.d(TAG, "No such document");
                    }

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
        //Log.d(TAG, retdoc[2]);
       // return retdoc;
    }

    public void showAlertbox(String title) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.alertbox_yes_no);
            dialog.setCanceledOnTouchOutside(true);

            EditText dispdiag = (EditText) dialog.findViewById(R.id.dispdiag);
            dispdiag.setText(String.format("Diagnosis:\n%s", title));



        dialog.show();
    }
}
