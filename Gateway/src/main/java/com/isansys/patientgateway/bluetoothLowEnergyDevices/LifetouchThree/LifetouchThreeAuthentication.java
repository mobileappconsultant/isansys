package com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree;

import androidx.annotation.NonNull;

import com.isansys.patientgateway.AES;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class LifetouchThreeAuthentication {

    private final RemoteLogging Log;
    private final static String TAG = LifetouchThreeAuthentication.class.getSimpleName();

    private final byte[] key;

    final int BLOCK_SIZE_BYTES = 16;
    byte[] input = new byte[BLOCK_SIZE_BYTES];
    byte[] output = new byte[BLOCK_SIZE_BYTES];

    public LifetouchThreeAuthentication(RemoteLogging logger, byte[] key) {
        this.Log = logger;
        this.key = key;
    }

    public byte[] encrypt(final byte[] data) throws Exception {
        return AES.encrypt(data, key);
    }

    public byte[] decrypt(final byte[] data) {
        try {
            System.arraycopy(data, 0, input, 0, BLOCK_SIZE_BYTES);
            output = AES.decrypt(input, key);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return output;
    }

    public byte[] decryptBlocks(final byte[] data) throws IllegalArgumentException {
        throwIfIllegalLength(data);

        byte[] outputBuffer = new byte[data.length];

        for (int i = 0; i < data.length; i += BLOCK_SIZE_BYTES) {
            byte[] block = decryptBlock(data, i);
            throwIfFailedToDecrypt(block);

            System.arraycopy(block, 0, outputBuffer, i, BLOCK_SIZE_BYTES);
        }

        return outputBuffer;
    }

    private void throwIfFailedToDecrypt(byte[] block) {
        if (block.length == 0)
            throw new IllegalArgumentException("AES failed");
    }

    private void throwIfIllegalLength(@NonNull final byte[] data) throws IllegalArgumentException {
        if (data.length == 0 || data.length % BLOCK_SIZE_BYTES != 0)
            throw new IllegalArgumentException("Length of array must be multiple of block size. Len = " + data.length + " block size = " + BLOCK_SIZE_BYTES);
    }

    @NonNull
    private byte[] decryptBlock(final byte[] data, int arrayIndex) {
        System.arraycopy(data, arrayIndex, input, 0, BLOCK_SIZE_BYTES);

        try {
            return AES.decrypt(input, key);
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
            return new byte[0];
        }
    }
}
