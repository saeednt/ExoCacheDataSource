package com.saeedentezari.exocachedatasource;

import android.content.Context;

import com.google.android.exoplayer.upstream.DataSpec;
import com.google.android.exoplayer.upstream.HttpDataSource;
import com.google.android.exoplayer.util.Assertions;

import junit.framework.Assert;

import java.util.List;
import java.util.Map;

public class CacheDataSource implements HttpDataSource {
	private final OkHttpDataSource okHttp1;
	private final OkHttpDataSource okHttp2;
	private boolean ok1Open = false;
	private byte[] bytes;


	private long endPos = 0;
	private long playerPos = 0;
	private boolean ok2Open;

	public CacheDataSource(Context context, OkHttpDataSource okHttp1, OkHttpDataSource okHttp2) {
		Assertions.checkNotNull(context);
		this.okHttp1 = Assertions.checkNotNull(okHttp1);
		this.okHttp2 = Assertions.checkNotNull(okHttp2);
		Assert.assertNotSame(okHttp1, okHttp2);
	}

	@Override
	public long open(DataSpec dataSpec) throws HttpDataSourceException {
		if (dataSpec.position <= endPos) {
			ok2Open = false;
			if (!ok1Open) {
				ok1Open = true;
				long l = okHttp1.open(dataSpec);
				bytes = new byte[(int) (dataSpec.position + l)];
				endPos = dataSpec.position;
				return l;
			}
			playerPos = dataSpec.position;
			return bytes.length - playerPos;
		} else {
			long l = okHttp2.open(dataSpec);
			playerPos = dataSpec.position;
			ok2Open = true;
			return l;
		}
	}

	@Override
	public void close() throws HttpDataSourceException {
		okHttp2.close();
		if (endPos == bytes.length) {
			okHttp1.close();
		}
	}

	@Override
	public int read(byte[] buffer, int offset, int readLength) throws HttpDataSourceException {
		if (!ok2Open) {
			if (playerPos == endPos) {
				int actualRead = okHttp1.read(buffer, offset, readLength);
				System.arraycopy(buffer, offset, bytes, (int) endPos, actualRead);
				endPos += actualRead;
				playerPos = endPos;
				return actualRead;
			} else {
				long diff = endPos - playerPos;
				readLength = (int) Math.min(diff, readLength);
				System.arraycopy(bytes, (int) playerPos, buffer, offset, readLength);
				playerPos += readLength;
				return readLength;
			}
		} else {
			return okHttp2.read(buffer, offset, readLength);
		}
	}

	@Override
	public void setRequestProperty(String name, String value) {
		okHttp1.setRequestProperty(name, value);
	}

	@Override
	public void clearRequestProperty(String name) {
		okHttp1.clearRequestProperty(name);
	}

	@Override
	public void clearAllRequestProperties() {
		okHttp1.clearAllRequestProperties();
	}

	@Override
	public Map<String, List<String>> getResponseHeaders() {
		return okHttp1.getResponseHeaders();
	}

	@Override
	public String getUri() {
		return okHttp1.getUri();
	}
}
