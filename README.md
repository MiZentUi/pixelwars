# Pixel Wars

![Banner](report/images/pixelwars.png)

**Pixel Wars** is a multiplayer game inspired by [r/place](https://www.reddit.com/r/place/). It provides an interface that allows users to interact with other players on the same map in real time.

The project is built with the **Spring Framework** and uses a **PostgreSQL** database. Authentication is handled via **Google OAuth 2.0**. For a more detailed description of the project, see the [report](report/course.pdf).

## Table of Contents

- [Install](#install)
  - [Configure Google OAuth](#configure-google-oauth)
  - [Build](#build)
- [Usage](#usage)
- [License](#license)

## Install

```
git clone https://github.com/MiZentUi/pixelwars.git
cd pixelwars
```

### Configure Google OAuth

Create a file named `application-secrets.properties` and paste your Google OAuth credentials.

Example `application-secrets.propeties`:

```properties
postgresql.username=user
postgresql.password=pass

oauth2.google.client-id=....apps.googleusercontent.com
oauth2.google.client-secret=...
```

### Build

This project requires [Maven](https://maven.apache.org/) as the build tool.

```
mvn clean package
```

## Usage

```
java -jar target/<package>.jar
```

After that, open [`localhost`](http://localhost) in your browser.

Additionally, you can change some settings in the [`application.properties`](src/main/resources/application.properties) file.

## License

[MIT © Denis Skrobot](LICENSE)