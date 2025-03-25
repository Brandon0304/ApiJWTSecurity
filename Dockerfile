FROM ubuntu:latest
LABEL authors="ortiz"

ENTRYPOINT ["top", "-b"]