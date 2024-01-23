# RDS Aurora Serverless Breakable Toy Scala

Experiment to make a Scala app that uses RDS Aurora Serverless.

## Running

```bash
./sbtx "run [access_key_id] [acces_key_secret]"
```

and make sure there is a file `src/main/resources/application.conf` with the following contents:

```hocon
host = "..." // e.g. "mydb.cluster-123456789012.us-east-1.rds.amazonaws.com"
port = 5432
username = "..." // e.g. "postgres"
password = "..." // e.g. "mypassword"
database = "..." // e.g. "mydb"
```
