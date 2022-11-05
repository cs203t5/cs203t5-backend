#!/bin/bash

echo 'Starting to Deploy...'

# copy nginx conf to default
sudo cp nginx.conf /etc/nginx/conf.d/default.conf

sudo systemctl restart nginx

sudo docker rm $(sudo docker stop $(sudo docker ps -a -q --filter ancestor='zdwong9/voxviridis'))

# remove all stopped docker containers
yes | sudo docker system prune -a 

# # remove all docker images 
sudo docker rmi -f $(sudo docker images -aq)

# build dockerfile
sudo docker pull zdwong9/voxviridis:latest

# run in detached mode
sudo docker run --env-file "../../dev.env" -e PUBLIC_KEY="$PUBLIC_KEY" -e PRIVATE_KEY="$PRIVATE_KEY" -p 8080:8080 -it zdwong9/voxviridis:latest

sleep 15

PORT=8080
checkHealth() {
    PORT=$1
    url="http://$HOSTNAME:$PORT/actuator/health"
    echo $url
    
    pingCount=0
    stopIterate=0
    loopStartTime=`date +%s`
    loopWaitTime=150 ## in seconds

    # Iterate till get 2 success ping or time out
    while [[ $pingCount -lt 2 && $stopIterate == 0 ]]; do
        startPingTime=`date +%s`
        printf "\ncurl -m 10 -X GET $url"
        curl -m 10 -X GET $url -o /dev/null 2>&1
        returnCode=$?
        if [ $returnCode = 0 ]
            then
            pingCount=`expr $pingCount + 1`
        fi
        endPingTime=`date +%s`
        pingTimeTaken=`echo " $endPingTime - $startPingTime " | bc -l`
        loopEndTime=`date +%s`
        loopTimeTaken=`echo " $loopEndTime - $loopStartTime " | bc -l`

        echo "Ping time is " $pingTimeTaken
        echo "ReturnCode is $returnCode"
        echo "PingCount is $pingCount "

        waitTimeEnded=`echo "$loopTimeTaken > $loopWaitTime" | bc -l`
        echo "LoopTimeTaken is $loopTimeTaken"
        echo "WaitTimeEnded is $waitTimeEnded"
        # On timeout, if 2 successfully pings not received, stop interaction
        if [[ $pingCount -lt 2 && "$waitTimeEnded" -eq 1 ]];
            then
            stopIterate=1
        fi
        sleep 5
    done

    if [ $stopIterate -eq 1 ]
    then
        if [ $pingCount -lt 2 ]
        then
            echo "PingCount is less than 2"
        else
            echo "Time taken in building took more than $loopWaitTime seconds"
        fi

        exit 1
    fi
}


checkHealth $PORT
checkHealthResponse=$?
if [ checkHealthResponse = 1 ]
    then
        echo "CheckHealth returns 1 that means something went wrong, exiting..."
        exit 1
else
    printf "\n\nService is running on $PORT ...\n\n"
fi

echo 'Deployment completed successfully'
