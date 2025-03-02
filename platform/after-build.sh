currentDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "$currentDir"
rm -rf "$currentDir"/backup/build
mkdir -p "$currentDir"/backup/build
/usr/bin/cp -r "$currentDir"/business/build "$currentDir"/backup
