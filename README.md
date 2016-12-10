# ExoCacheDataSource
With this datasource you can easily cache your mp4 files in memory to prevent refetching data over http and causing big pauses in seek back of media.

This class will keep the mp4 fetched from the server in memory and if you seek forward or backward the cache will not be deleted and will be read from the memory.
