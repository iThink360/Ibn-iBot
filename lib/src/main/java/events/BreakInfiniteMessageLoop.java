/** Class to prevent infinite message loops from iBot. */

package events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BreakInfiniteMessageLoop extends ListenerAdapter
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if (event.getAuthor().equals(event.getJDA().getSelfUser()))
			return;
	}
}
