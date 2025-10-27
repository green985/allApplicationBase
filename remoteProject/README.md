# Google Cloud Functions - Kotlin

## Build

### Root project'ten

```bash
cd /Users/balbazar/Desktop/Project/allApplicationBase
./gradlew :remoteProject:shadowJar
```

### Standalone (remoteProject içinden)

```bash
cd /Users/balbazar/Desktop/Project/allApplicationBase/remoteProject
./gradlew clean shadowJar
```

Çıktı: `remoteProject/build/libs/remoteproject.jar`

## Deploy

### HTTP Function

```bash
gcloud functions deploy hello-function \
  --gen2 \
  --runtime=java17 \
  --region=europe-west1 \
  --source=/Users/balbazar/Desktop/Project/allApplicationBase/remoteProject \
  --entry-point=com.balbazar.remoteproject.HelloFunction \
  --trigger-http \
  --allow-unauthenticated
```

### Pub/Sub Function

```bash
gcloud functions deploy pubsub-function \
  --gen2 \
  --runtime=java17 \
  --region=europe-west1 \
  --source=remoteProject \
  --entry-point=com.balbazar.remoteproject.PubSubFunction \
  --trigger-topic=YOUR_TOPIC_NAME
```

## Test

### HTTP GET

```bash
curl "https://REGION-PROJECT_ID.cloudfunctions.net/hello-function?name=Kotlin"
```

### HTTP POST

```bash
curl -X POST https://REGION-PROJECT_ID.cloudfunctions.net/hello-function \
  -H "Content-Type: application/json" \
  -d '{"name":"Kotlin User","action":"test"}'
```

## Local Test

Functions Framework kullanarak local test:

```bash
./gradlew :remoteProject:runFunction -Prun.functionTarget=com.balbazar.remoteproject.HelloFunction
```

Ardından: `http://localhost:8080`

---

## Quick Deploy

### 1. Clean Build (Standalone)

```bash
cd /Users/balbazar/Desktop/Project/allApplicationBase/remoteProject
./gradlew clean shadowJar
```

### 2. Deploy to Google Cloud

```bash
gcloud functions deploy hello-function \
  --gen2 \
  --runtime=java17 \
  --region=europe-west1 \
  --source=/Users/balbazar/Desktop/Project/allApplicationBase/remoteProject \
  --entry-point=com.balbazar.remoteproject.HelloFunction \
  --trigger-http \
  --allow-unauthenticated \
  --project=quoteapplication-45b67
```

### 3. Test Deployed Function

```bash
curl "https://europe-west1-quoteapplication-45b67.cloudfunctions.net/hello-function?name=Test"
```

**Not:** Deploy sırasında Maven (`pom.xml`) kullanılır, Gradle sadece local development için.
