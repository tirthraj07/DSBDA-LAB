# Locally Setup Apache Spark on Ubuntu

To set up Apache Spark on Ubuntu, here is a step-by-step guide starting from scratch. I'll avoid assuming any prior installation and will guide you through all the necessary dependencies and environment variables.

### Step 1: Update your package lists
Start by updating your package lists to ensure you get the latest versions of all packages.
```bash
sudo apt update
```

### Step 2: Install Java
Apache Spark requires Java to be installed, as it is built on the JVM. We’ll install OpenJDK 8, which is commonly used with Spark.
```bash
sudo apt install openjdk-8-jdk -y
```

Check if Java has been installed successfully:
```bash
java -version
```
It should return the Java version installed.

### Step 3: Install Scala
Apache Spark is written in Scala, so you'll also need Scala installed.
```bash
sudo apt install scala -y
```

Check if Scala is installed:
```bash
scala -version
```

### Step 4: Download and Install Apache Spark
Now, we can download Apache Spark. Go to the [official Apache Spark website](https://spark.apache.org/downloads.html) to check for the latest stable version. Use `wget` to download it.

```bash
cd /opt
sudo wget https://dlcdn.apache.org/spark/spark-3.5.5/spark-3.5.5-bin-hadoop3.tgz
```

Extract the Spark tar file:
```bash
sudo tar -xvzf spark-3.5.5-bin-hadoop3.tgz
```

Remove the tar file (optional, to save space):
```bash
sudo rm spark-3.5.5-bin-hadoop3.tgz
```

### Step 5: Set up Environment Variables
Now, let’s configure the necessary environment variables for Apache Spark.

1. Open your `~/.bashrc` file to edit it:
   ```bash
   nano ~/.bashrc
   ```

2. Add the following lines at the end of the file to set the `SPARK_HOME` and `PATH` variables:

    ```bash
    export SPARK_HOME=/opt/spark-3.5.5-bin-hadoop3
    export PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
    export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
    export SCALA_HOME=/usr/share/scala
   ```

3. Source the updated `.bashrc` file to apply the changes:
   ```bash
   source ~/.bashrc
   ```

4. Verify the Update
    Now, verify that the environment variables have been updated:
    ```bash
    echo $SPARK_HOME
    echo $PATH
    ```
    You should see something like
    ```
    /opt/spark-3.5.5-bin-hadoop3
    /opt/spark-3.5.5-bin-hadoop3/bin:/opt/spark-3.5.5-bin-hadoop3/sbin:...
    ```

### Step 6: Verify the Installation
To verify that Spark is installed correctly, run the following command:
```bash
spark-shell
```
This should start the Spark shell, and you should be able to interact with Spark using Scala.

Expected Output
```
Setting default log level to "WARN".
To adjust logging level use sc.setLogLevel(newLevel). For SparkR, use setLogLevel(newLevel).
25/04/27 17:37:43 WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Spark context Web UI available at http://ip-172-31-12-196.ec2.internal:4040
Spark context available as 'sc' (master = local[*], app id = local-1745775465202).
Spark session available as 'spark'.
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 3.5.5
      /_/
         
Using Scala version 2.12.18 (OpenJDK 64-Bit Server VM, Java 1.8.0_442)
Type in expressions to have them evaluated.
Type :help for more information.

```