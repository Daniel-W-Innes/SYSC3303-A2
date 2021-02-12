This is a markdown file but I can't submit it as such because I'll lose marks.

# SYSC 3303 Assignment 2

Example code for Sending receiving and proxying udp messages as well as decoding in encoding objects .

## Important Notes
* There is regex for strings in the request.
* UTF-8 is used for encoding strings.
* If the strings in the request too long it can overflow the buffer in the receiver.
* See the doc folder for more documentation.

## Files
* model is the date model for sending between actors. All data models have the ability to decode in encode themselves.
  - Request.java is a model containing information to send to the server.
  - BadRequest.java is an extension of the request object with a broken encoder.
  - Response.java is a model containing information to send to the client.
* actor is the actors that exchange udp messages.
    - Client.java sends requests and prints the response.
    - Intermediate.java a proxy between the server and client.
    - Server.java receives requests and responses to them.
   

## Ports
* 25 for the intermediate
* 69 for the server
 
## Running
Run main from Main.java or each actor independently.

## Author Info
Name: Daniel Innes

Student number: 101067175