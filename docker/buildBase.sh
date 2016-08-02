DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROXY_ARGS="--build-arg HTTP_PROXY=$http_proxy --build-arg http_proxy=$http_proxy --build-arg HTTPS_PROXY=$https_proxy --build-arg https_proxy=$https_proxy"

echo "Bulding base"
cd $DIR/base
docker build --rm -t gaffer-docker/base $PROXY_ARGS .
echo "Completed building base"

cd $DIR
