/**
 * A simple And inefficient proxy. It suggested that any real system use out of the box proxy instead of implementing their own.
 * <p>
 * This implementation assumes that the outline is specifying the same print statement multiple times.
 * <ul>
 *     <li>the host forms a packet to send containing exactly what it received</li>
 *     <li>the host prints out this information</li>
 *     <li>the host forms a packet to send containing exactly what it received the host prints out this information</li>
 * </ul>
 * It would be completely illogical to form I pocket twice and print the information twice as such ot is assumed that it is only necessary to print the request and response.
 */
package actor.intermediate;