/** Event listener for shutting down the bot. */

package main;

import java.awt.Color;
import java.time.Instant;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Kill extends ListenerAdapter
{
	private static final Color KILL_EMBED = new Color(0x00000f);
	
	@Override
	public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event)
	{
		// Validate prerequisites
		if (!event.getAuthor().equals(Main.getOwner()))
			return;
		if (!event.getMessage().getContentRaw().equalsIgnoreCase(Main.getPrefix() + "kill"))
			return;
		
		// Success
		EmbedBuilder DM = new EmbedBuilder()
				.setColor(KILL_EMBED)
				.setTitle("🚫 Kill process executed successfully.")
				.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
				.setFooter(event.getJDA().getSelfUser().getAsTag(), event.getJDA().getSelfUser().getAvatarUrl())
				.setTimestamp(Instant.now());
		
		event.getChannel().sendMessageEmbeds(DM.build()).queue();
		event.getJDA().shutdown();
	}
}
