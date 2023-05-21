# MyMCServer
A system that allows you to have real-time information on a minecraft server and to send commands via chat.
This plugin will be useful only for OPs players.

## WebServer Protocol
**Request Type**: HTTP
**Answer Type**: JSON
**Response Charset**: ISO_8859_1
### API Documentation:
- **Get the token of the user**:
    - **Request**:
        - "GET <SERVER_ADDRESS>/login HTTP/1.1"
        - **Request Headers**:
          - "MMCS-Username":<USERNAME>
          - "MMCS-Password":<PASSWORD>
    - **Response**:
```json
      {
        "return-code":<RETURN_CODE>,
        "token":<TOKEN>
      }
```
- **Send a command**:
    - **Request**:
        - "GET <SERVER_ADDRESS>/command HTTP/1.1"
        - **Request Headers**:
          - "MMCS-Token":<TOKEN>
          - "MMCS-Command":<COMMAND>
    - **Response**:
```json
      {
        "return-code":<RETURN_CODE>,
        "command-output":<COMMAND_OUTPUT>
      }
```
- **Get server stats**:
    - **Request**:
        - "GET <SERVER_ADDRESS>/stats HTTP/1.1"
        - **Request Headers**:
          - "MMCS-Token":<TOKEN>
    - **Response**:
```json
      {
        "return-code":<RETURN_CODE>,
        "system-cpu-load":"<SYSTEM_CPU_LOAD>,
        "process-cpu-load":<PROCESS_CPU_LOAD>,
        "total-memory":<TOTAL_MEMORY>,
        "free-memory":<FREE_MEMORY>,
        "committed-virtual-memory":<COMMITTED_VIRTUAL_MEMORY>,
        "player-list":[
          {
            "name":"<PLAYER_NAME>",
            "online":true|false
          }
        ]
        "addons-list":[
          {
            "name":"<ADDON_NAME>",
            "enabled":true|false
          }
        ]
      }
```
