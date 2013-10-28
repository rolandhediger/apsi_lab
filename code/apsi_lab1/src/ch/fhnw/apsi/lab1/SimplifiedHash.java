package ch.fhnw.apsi.lab1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.PaddedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public class SimplifiedHash {
	private final static byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0 };

	private byte[] preprocess(byte[] input) {
		int mLength = input.length;
		byte[] length = ByteBuffer.allocate(8).putLong(mLength).array();
		int r = mLength % 8; // input length % 64 bit blocks
		byte[] out = new byte[mLength + r + 8];

		// Copy
		for (int i = 0; i < mLength; i++)
			out[i] = input[i];
		// padding
		if (r > 0)
			out[mLength] = -128; // 0b1000 0000

		for (int i = 1; i < r; i++)
			out[mLength + i] = 0;

		// add message length
		for (int i = 0; i < 8; i++)
			out[out.length - 8 + i] = length[i];

		return out;
	}

	private long create(byte[] input) {
		BlockCipher engine = new DESEngine();
		@SuppressWarnings("deprecation")
		BufferedBlockCipher cipher = new PaddedBlockCipher(engine);
		byte[] desOut = new byte[16];
		byte[] hash = new byte[8];
		byte[] previousHash = iv.clone();

		for (int i = 0; i < input.length; i += 8) {
			KeyParameter p = new KeyParameter(previousHash);
			cipher.init(true, p);
			desOut = new byte[cipher.getOutputSize(8)];
			int outputLen = cipher.processBytes(input, i, 8, desOut, 0);

			try {
				cipher.doFinal(desOut, 0);

				// xor magix
				for (int j = 0; j < hash.length; j++)
					hash[j] = (byte) ((desOut[j] ^ desOut[j + 8]) ^ previousHash[j]);

				// swap
				byte[] tmp = hash;
				hash = previousHash;
				previousHash = tmp;

			} catch (CryptoException ce) {
				System.err.println(ce);
			}

		}

		ByteBuffer buffer = ByteBuffer.wrap(previousHash);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.getLong();
	}

	public int createHash(byte[] input) {
		long hash = this.create(this.preprocess(input));
		int h1 = (int) (hash >>> 32);
		int h2 = Integer.reverse((int) hash);

		return h1 ^ h2;
	}

}
