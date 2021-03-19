sudo apt update
sudo apt-get install git -y
sudo apt install default-jdk -y
sudo apt-get install maven -y
sudo apt-get install wget -y
sudo apt-get install unzip -y

git clone https://github.com/Kayathiri-Mahendrakumaran/Distributed_Content_Searching.git

cd ~/Distributed_Content_Searching/Bootstrap Server/Java
# Download ngrok
wget -q -c -nc https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip
unzip -qq -n ngrok-stable-linux-amd64.zip

./ngrok authtoken 1lFRSvwN1ZY2PTQ7VCmb5R9zMps_6WLtuEH9q1WauJeYeeKsZ
./ngrok http 55555
java BootstrapServer

cd Distributed_Content_Searching/FileSharingSystem/
mvn clean install
cd target/
ls
java -jar FileSharingSystem-1.0-SNAPSHOT.jar
#set up port forwarding on UDP from port 12345 to port 54321
sudo iptables -t nat -A PREROUTING -p udp -d 35.184.91.113 --dport 55555 -j DNAT --to 127.0.0.1:55555
sudo iptables -A FORWARD -p udp -d 35.184.91.113 --dport 55555 -m state --state NEW,ESTABLISHED,RELATED -j ACCEPT