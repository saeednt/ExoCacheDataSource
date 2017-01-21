# ExoCacheDataSource
With this datasource you can easily cache your mp4 files in memory to prevent refetching data over http and causing big pauses in seek back of media.

This class will keep the mp4 fetched from the server in memory and if you seek forward or backward the cache will not be deleted and will be read from the memory.

# Usage
You should define the datasource with two different okhttpdatasources as follows:
```java
OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(new okhttp3.Cache(context.getCacheDir(), 1024000)).build();
OkHttpDataSource okHttpDataSource = new OkHttpDataSource(okHttpClient, "android", null);
OkHttpDataSource ok2 = new OkHttpDataSource(okHttpClient, "android", null);
HttpDataSource dataSource = new CacheDataSource(context, okHttpDataSource, ok2);
```

Then you create an ExtractorSampleSource as follows:
```java
ExtractorSampleSource sampleSource = new ExtractorSampleSource(
				uri,
				dataSource,
				allocator,
				buffer_segment_count * buffer_segment_size,
				new Mp4Extractor(), new Mp3Extractor());
```

And the Rest of the ordinary procedures which you take to stream a file:
```java
ExoPlayer mPlayer = ...;

videoTrackRenderer = new MediaCodecVideoTrackRenderer(context, sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 2000, new Handler(context.getMainLooper()), this, 1000);
audioTrackRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT, new Handler(context.getMainLooper()), this);

mPlayer.prepare(videoTrackRenderer, audioTrackRenderer);    
```
