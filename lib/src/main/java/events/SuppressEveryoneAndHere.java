/** Suppresses @everyone and @here for those who don't have the perms. */

package events;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SuppressEveryoneAndHere extends ListenerAdapter
{
	private static final Color SUPPRESS_EVERYONE_HERE_EMBED = new Color(0xffff00);
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		Member sender = event.getMember();
		
		if (event.getAuthor().isBot())
			return;
		
		if (sender.isOwner() || sender.hasPermission(Permission.ADMINISTRATOR) || sender.hasPermission(Permission.MESSAGE_MENTION_EVERYONE))
			return;
		
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		
		boolean mentionsEveryone = false;
		boolean mentionsHere = false;
		
		for (String str : args)
		{
			if (str.indexOf("@everyone") != -1)
				mentionsEveryone = true;
			else if (str.indexOf("@here") != -1)
				mentionsHere = true;
		}
		
		if (!mentionsEveryone && !mentionsHere)
			return;
		
		EmbedBuilder reminder = new EmbedBuilder()
				.setColor(SUPPRESS_EVERYONE_HERE_EMBED)
				.setDescription("Pinging @everyone and/or @here is strictly prohibited.")
				.setFooter("Even if your permissions are disabled, attempting to ping " + event.getGuild().getMemberCount() + " members for absolutely no reason is rather cruel. Repetitive offenses can be subject to punishment. YOU HAVE BEEN OFFICIALLY WARNED.");
		
		if (mentionsEveryone && mentionsHere)
			reminder.setTitle("⚠ @everyone and @here Detected");
		else if (mentionsEveryone && !mentionsHere)
			reminder.setTitle("⚠ @everyone Detected");
		else if (!mentionsEveryone && mentionsHere)
			reminder.setTitle("⚠ @here Detected");
		
		event.getMessage().delete().queue();
		event.getChannel().sendMessage(sender.getAsMention()).setEmbeds(reminder.build()).queue();
	}
}
