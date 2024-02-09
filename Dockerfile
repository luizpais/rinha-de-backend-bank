FROM ubuntu:latest
LABEL authors="luizp"

ENTRYPOINT ["top", "-b"]