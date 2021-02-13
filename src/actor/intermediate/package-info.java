/**
 * A simple and inefficient (compared to a corporate solution) proxy. It suggested that any real system use an out of the box proxy instead of implementing their own.
 * <p>
 * The proxy is split into multiple components to increase efficiency.
 * It has a distinct frontend to handle requests from clients without blocking to wait for the server.
 * The load balancer allows for multiple backends to be associated with a single frontend.
 * The backend is the slowest of the 3 components because it needs to wait for the servers response.
 * <p>
 * This implementation assumes that the outline is specifying the same print statement multiple times.
 * <ul>
 *     <li>the host forms a packet to send containing exactly what it received</li>
 *     <li>the host prints out this information</li>
 *     <li>the host forms a packet to send containing exactly what it received the host prints out this information</li>
 * </ul>
 * It would be completely illogical to form the packet twice and print the information twice as such it is assumed that it is only necessary to print the request and response.
 */
package actor.intermediate;