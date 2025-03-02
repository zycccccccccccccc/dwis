FROM swr.cn-east-2.myhuaweicloud.com/hciot/java

MAINTAINER lasdaybg

ADD /build/bootScripts /business/bin
ADD /build/libs /business/lib

WORKDIR /business

EXPOSE 9081

HEALTHCHECK --interval=5s --timeout=3s --start-period=180s CMD wget -q -O - http://localhost:9081/dwis/actuator || exit 1

CMD /business/bin/business
