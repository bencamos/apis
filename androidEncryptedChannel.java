@Override
    protected void onResume() {
            handler.postDelayed ( runnable = new Runnable () {
                @Override
                public void run() {
                        final Handler handler = new Handler ();
                        Thread thread = new Thread ( new Runnable () {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        SQLiteDatabase mydatabase = openOrCreateDatabase("data",MODE_PRIVATE,null);
                                        Cursor resultSet = mydatabase.rawQuery ( "Select * from data", null );
                                        resultSet.moveToFirst ();
                                        Integer doChatFunction = resultSet.getInt ( resultSet.getColumnIndex ( "chatFunction"));
                                        resultSet.close();
                                        if (doChatFunction == 1) {
                                            BufferedReader input = new BufferedReader ( new InputStreamReader ( ConnectThread.getInstance ().getSocket ().getInputStream () ) );
                                            final String st = input.readLine ();
                                            if (st == "User has connected") {
                                                String c = mTextViewReplyFromServer.getText ().toString ();
                                                mTextViewReplyFromServer.setText ( c + "\nNew User Has Connected" );
                                            }
                                            handler.post ( new Runnable () {
                                                @SuppressLint("SetTextI18n")
                                                @RequiresApi(api = Build.VERSION_CODES.O)
                                                @Override
                                                public void run() {
                                                    String s = mTextViewReplyFromServer.getText ().toString ();
                                                    if (st.trim ().length () != 0)
                                                        triggerNotification ();
                                                    mTextViewReplyFromServer.setText ( s + "\nDecrypted: " + decrypt ( st, secretKey ) );
                                                }
                                            } );
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace ();
                                }
                            }
                        } );
                        thread.start ();
                }
            }, delay );
            super.onResume ();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks ( runnable );
        super.onPause ();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String a = mTextViewReplyFromServer.getText().toString();
                final String b = mEditTextSendMessage.getText().toString();
                mEditTextSendMessage.setText("");
                mTextViewReplyFromServer.setText(a + "\nEncrypted: " + b);
                //final Handler handler = new Handler();
                Thread thread = new Thread() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void run() {
                        try {
                            ConnectThread.getInstance().sendMessage(encrypt(b, secretKey) );
                        } catch (Exception e) {
                            e.printStackTrace ();
                        }
                    }
                };
                thread.start();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec (secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String strToDecrypt, String secret) {
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
