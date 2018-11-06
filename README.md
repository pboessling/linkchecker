# Linkchecker

Checks the status codes of a given list of links.

## Requirements

- JDK 8

## How to run

- Configure a list of urls to check in `/src/main/resources/input.csv`. E.g.:
    ```
    http://example.com
    http://example.com
    http://example.com
    ```
- Start the application with the following command:
    ```
    mvnw spring-boot:run
    ```
- A file `/output.csv` will be created, which lists the urls with their respective status code.
    ```
     http://example.com;200
     http://example.com;200
     http://example.com;200   
    ```

## License

[MIT](LICENSE)

## Author Information

Created by [Philippe Bößling](https://www.gihub.com/pboessling).