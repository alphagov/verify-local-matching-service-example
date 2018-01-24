FROM govukverify/java8:latest

RUN mkdir /matchingservice
COPY . /matchingservice
WORKDIR /matchingservice

# Be careful not to couple this file with the base image
# in case of upstream changes - in fact, you should prefer not to
# edit this dockerfile if possible.