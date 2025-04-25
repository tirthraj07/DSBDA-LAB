Summary of key steps

1. Prerequisites: update Ubuntu, install OpenJDK 8, set up passwordless SSH.
2. Download & install Hadoop 3.3.6 from Apache mirrors; verify checksums.
3. Configure environment variables (.bashrc, hadoop-env.sh).
4. Configure HDFS (core-site.xml, hdfs-site.xml) for one NameNode + two DataNode instances via duplicated workers entries.
5. Format HDFS, start DFS & YARN daemons; verify with jps.
6. Write WordCount Java (Mapper, Reducer, Driver), compile into JAR.
7. Run the job: push input to HDFS, launch hadoop jar …, fetch output.

# Step 1: Update package lists
```bash
sudo apt update && sudo apt upgrade -y
```
---
# Step 2: Install OpenJDK 8 (build requirement for Hadoop) and OpenSSH (for daemon communication)
```bash
sudo apt install -y openjdk-8-jdk openssh-server openssh-client
```

Verify Java
```bash
java -version  

# should report “1.8.0_xxx”  
openjdk version "1.8.0_442"
OpenJDK Runtime Environment (build 1.8.0_442-8u442-b06~us1-0ubuntu1~24.04-b06)
OpenJDK 64-Bit Server VM (build 25.442-b06, mixed mode)
```
---
# Step 3: Enable passwordless SSH to localhost

## 3.1 Create a new RSA key pair with no password, and save it to a file.
```bash
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
```
## 3.2 Add the public key to the list of allowed keys (so you can log in to this machine via SSH without a password).
```bash
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
```

## 3.3 Make sure only you can read/write the authorized_keys file
```bash
chmod 600 ~/.ssh/authorized_keys
```
---
# Step 4: Download & Install Hadoop 3.3.6

## 4.1 Fetch the binary tarball from an Apache mirror:
```bash
wget https://downloads.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz
```

```
# It should download the tar file
Length: 730107476 (696M) [application/x-gzip]
Saving to: ‘hadoop-3.3.6.tar.gz’
hadoop-3.3.6.tar.gz    100%[=======================>] 696.28M  17.4MB/s    in 15m 48s 
2025-04-24 18:31:46 (752 KB/s) - ‘hadoop-3.3.6.tar.gz’ saved [730107476/730107476]
```

## 4.2 Unpack the file
```bash
tar -xvzf hadoop-3.3.6.tar.gz
```

## 4.3 Move it to the right location
> Move the Hadoop folder to `/usr/local`, a standard place for software on Linux.
```bash
sudo mv hadoop-3.3.6 /usr/local/hadoop
```

## 4.4 Make a log folder
> Create a folder for Hadoop logs
```bash
sudo mkdir -p /usr/local/hadoop/logs
```

## 4.5 Give yourself ownership
> Make yourself the owner of the Hadoop folder so you can access/edit everything.
```bash
sudo chown -R $(whoami):$(whoami) /usr/local/hadoop
```
---
# Step 5: Configure Environment Variables

## 5.1 Edit `~/.bashrc`
```bash
sudo nano ~/.bashrc
```

## 5.2 Append the following environment variables at the end
```bash
export HADOOP_HOME=/usr/local/hadoop
export HADOOP_INSTALL=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
```

## 5.3 Reload
```bash
source ~/.bashrc
```

Verify
```bash
echo $JAVA_HOME
/usr/lib/jvm/java-8-openjdk-amd64
echo $HADOOP_HOME
/usr/local/hadoop
echo $YARN_HOME
/usr/local/hadoop
```
## 5.4 Set the environment in Hadoop Environment
```bash
sudo nano $HADOOP_HOME/etc/hadoop/hadoop-env.sh
```

Append the following
```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HADOOP_LOG_DIR=$HADOOP_HOME/logs
export HADOOP_PID_DIR=/tmp/hadoop-pids
```
---
# Step 6: Configure HDFS

## 6.1 Verify Hadoop Installation
```bash
hadoop version
```

Example output
```
Hadoop 3.3.6
Source code repository https://github.com/apache/hadoop.git -r 1be78238728da9266a4f88195058f08fd012bf9c
Compiled by ubuntu on 2023-06-18T08:22Z
Compiled on platform linux-x86_64
Compiled with protoc 3.7.1
From source with checksum 5652179ad55f76cb287d9c633bb53bbd
This command was run using /usr/local/hadoop/share/hadoop/common/hadoop-common-3.3.6.jar
```


## 6.2 Edit the `core-site.xml` configuration
```bash
sudo nano $HADOOP_HOME/etc/hadoop/core-site.xml
```
> This file is part of Hadoop’s configuration files. It's used to set core settings that Hadoop needs to run.

Add the following inside the file

```xml
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9000</value>
  </property>
</configuration>
```

> `Ctrl + S` to Save and `Ctrl + X` to Exit nano editor

`<name>fs.defaultFS</name>` tells Hadoop what file system to use by default. (File System default)
`<value>hdfs://localhost:9000</value>` tells hadoop to use the Hadoop Distributed File System (HDFS), and connect to it on this computer (localhost) using port 9000.

> "Hadoop, whenever I work with files, use your own file system (HDFS) and talk to the NameNode on my own machine at port 9000."


## 6.3 Edit the `hdfs-site.xml`

```bash
sudo nano $HADOOP_HOME/etc/hadoop/hdfs-site.xml
```
> This file configures how Hadoop's storage system (HDFS) works on your machine. You're telling Hadoop where to store data and how to manage it.

Add the following inside the file
```xml
<configuration>

  <property>
    <name>dfs.namenode.name.dir</name>
    <value>file:///usr/local/hadoop/hdfs/namenode</value>
  </property>
  
  <property>
    <name>dfs.datanode.data.dir</name>
    <value>file:///usr/local/hadoop/hdfs/datanode</value>
  </property>
  
  <property>
    <name>dfs.replication</name>
    <value>2</value>
  </property>

</configuration>
```

`<name>dfs.namenode.name.dir</name>` tells Hadoop to store NameNode information (like a directory of all the files) here  
`<name>dfs.datanode.data.dir</name>` tells Hadoop to store the actual file chunks - datanode 
`<name>dfs.replication</name>` tells Hadoop to keep 2 copies of every piece of data

## 6.4 Create those directories

```bash
mkdir -p $HADOOP_HOME/hdfs/{namenode,datanode}
```

## 6.5 Edit workers file
```bash
sudo nano $HADOOP_HOME/etc/hadoop/workers
```
Insert the following
```
localhost
```
---
# Step 7: Format HDFS & Start Daemons

## 7.1 Format the NameNode
```bash
hdfs namenode -format
```
> This command initializes the NameNode. It creates the metadata structure — like a brand-new table of contents for your Hadoop file system

Verify
```bash
cd $HADOOP_HOME/hdfs/namenode
```

You should see directory structure something like this
```
namenode
└── current
    ├── VERSION
    ├── fsimage_0000000000000000000
    ├── fsimage_0000000000000000000.md5
    └── seen_txid
```

## 7.2 Start the HDFS Daemons
```bash
$HADOOP_HOME/sbin/start-all.sh
```

Expected Output
```
WARNING: Attempting to start all Apache Hadoop daemons as ubuntu in 10 seconds.
WARNING: This is not a recommended production deployment configuration.
WARNING: Use CTRL-C to abort.
Starting namenodes on [localhost]
Starting datanodes
Starting secondary namenodes [ip-172-31-28-20]
ip-172-31-28-20: Warning: Permanently added 'ip-172-31-28-20' (ED25519) to the list of known hosts.
Starting resourcemanager
Starting nodemanagers
```

## 7.3 Verify that Namenode and Datanodes have started

```bash
jps
```

Expected output:
```
20320 NodeManager
19667 DataNode
20725 Jps
20105 ResourceManager
19468 NameNode
19901 SecondaryNameNode
```

## 7.4 Check the Web Interfaces

<table>
<tr>
<td>PORT</td>
<td>URL</td>
<td>UI Name</td>
<td>Description</td>
</tr>

<tr>
<td>9870</td>
<td>http://localhost:9870/</td>
<td>NameNode Web UI</td>
<td>HDFS health (live/dead datanodes), File browser for HDFS, Configuration details, etc</td>
</tr>

<tr>
<td>8088</td>
<td>http://localhost:8088/</td>
<td>ResourceManager Web UI (YARN)</td>
<td>Running and completed YARN applications (MapReduce, Spark, etc.), Job history, etc</td>
</tr>

<tr>
<td>8042</td>
<td>http://localhost:8042/</td>
<td>NodeManager Web UI</td>
<td>Status of containers on that node, Logs for MapReduce or other YARN jobs running on the node</td>
</tr>

</table>


# Step 8: Create a Jar file

Create a WordCount.java file and Paste the following code
> 
```java
// see full code in WordCount.java file
public class WordCount { … }
```

Compile and package into a JAR:
```bash
mkdir wc_classes
javac -classpath $(hadoop classpath) -d wc_classes WordCount.java
jar -cvf wordcount.jar -C wc_classes/ .
```

Or use the Maven project to compile it -> Refer `Maven WordCount Project`
```bash
mvn clean package
```

You should see the wordcount.jar file
```bash
ls
```
```
# Output:
WordCount.java  wc_classes  wordcount.jar
```

# Step 9: Create Input file
```bash
nano input.txt
```

```
hello world hello hadoop
This is Tirthraj Mahajan
Tirthraj studies in Pune Institute of Computer Technology
Java is OOP Language
Chelsea is the greatest football club
```


# Step 9: Run Your WordCount Job

## 9.1 Upload input
```bash
hdfs dfs -mkdir -p /user/tirthraj/input
hdfs dfs -put input.txt /user/tirthraj/input/
```

## 9.2 Run the map reduce on all the uploded input file and create a output directory

```bash
hadoop jar wordcount.jar WordCount /user/tirthraj/input /user/tirthraj/output
```

## 9.3 Retrieve results
```bash
hdfs dfs -ls /user/tirthraj/output
hdfs dfs -cat /user/tirthraj/output/part-r-00000
```

Output
```
Chelsea         1
Computer        1
Institute       1
Java            1
Language        1
Mahajan         1
OOP             1
Pune            1
Technology      1
This            1
Tirthraj        2
club            1
football        1
greatest        1
hadoop          1
hello           2
in              1
is              3
of              1
studies         1
the             1
world           1
```