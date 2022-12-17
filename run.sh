docker build . -t nanolyze-tiff-analyzer
docker run -p8080:8080 nanolyze-tiff-analyzer:latest nanolyze
