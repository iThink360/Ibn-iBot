/** Event listener for filtering links. */

package events;

import java.net.URL;

import javax.annotation.Nonnull;

import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LinkFilter extends ListenerAdapter
{
	// Unlimited access for these roles
	private static final long[] validRoles = {
			824642393500024853L, // Twitch Subscriber
			766015356313141290L, // Booster
			780853603216261131L, // Content Creator
			815332078009712640L, // Trial Moderator
			822926227848429600L, // Tournament Staff
			768924103222231071L, // Moderator
			834639923327270912L, // Super Moderator
			766032286394875954L, // Legend
			766752351393546240L, // Administrator
			766903008172048384L  // Owner
	};
	
	// For Trusted members
	private static final long TRUSTED = 766938369040318494L;
	private static final long[] validTextChannels = {
			766887394300919808L, // #tech-talk
			766887725763657730L  // #promo
	};
	
	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
	{
		if (event.getAuthor().isBot())
			return;
		
		if (memberIsValid(event))
			return;
		
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		Member sender = event.getMember();
		
		// Filter links
		for (int i = 0; i < args.length; i++)
		{
			try
			{
				@SuppressWarnings("unused")
				URL url = new URL(args[i]);
				
				if (sender.getRoles().contains(event.getGuild().getRoleById(TRUSTED)) && args[i].indexOf("tenor.com") != -1 || args[i].indexOf(".gif") != -1 || args[i].indexOf(".jpg") != -1 || args[i].indexOf(".jpeg") != -1 || args[i].indexOf(".png") != -1)
					continue;
				
				else
				{
					event.getMessage().delete().complete();
					
					EmbedBuilder denied = new EmbedBuilder()
							.setColor(Main.getDefaultEmbedColor())
							.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
							.setTitle("❌ Access Denied")
							.setDescription("You are not allowed to send links.")
							.addField("__Unlimited Link Access:__ To send links anywhere, you MUST have at least one of these roles:",
									event.getGuild().getRoleById(validRoles[0]).getAsMention() + "\n" +
									event.getGuild().getRoleById(validRoles[1]).getAsMention() + "\n" +
									event.getGuild().getRoleById(validRoles[2]).getAsMention() + "\n" +
									event.getGuild().getRoleById(validRoles[3]).getAsMention() + "\n" +
									event.getGuild().getRoleById(validRoles[4]).getAsMention() + "\n" +
									event.getGuild().getRoleById(validRoles[5]).getAsMention() + "\n" +
									event.getGuild().getRoleById(validRoles[6]).getAsMention() + "\n" +
									event.getGuild().getRoleById(validRoles[7]).getAsMention() + "\n" +
									event.getGuild().getRoleById(validRoles[8]).getAsMention() + "\n" +
									event.getGuild().getRoleById(validRoles[9]).getAsMention(), false)
							.addField("__Limited Link Access:__ If you do not have a premium role but are a Trusted member, you can ONLY send links in the following channels:", 
									event.getGuild().getTextChannelById(validTextChannels[0]).getAsMention() + ": For tech resources only\n" + 
									event.getGuild().getTextChannelById(validTextChannels[1]).getAsMention() + ": For self promotion only\n", false)
							.setFooter("LINKS THAT VIOLATE THESE POLICIES WILL BE PURGED AND REPETITIVE INFRACTIONS MAY BE SUBJECT TO PUNISHMENT");
					
					event.getChannel().sendMessage(sender.getAsMention()).setEmbeds(denied.build()).queue();
					return;
				}
			}
			catch (Exception e)
			{
				continue;
			}
		}
	}
	
	// Check for validity of links
	private static boolean memberIsValid(GuildMessageReceivedEvent event)
	{
		Member sender = event.getMember();
		
		if (sender.isOwner() || sender.hasPermission(Permission.ADMINISTRATOR))
			return true;
		else
		{
			// If special member, return true
			for (int i = 0; i < validRoles.length; i++)
				if (sender.getRoles().contains(Main.getMgl().getRoleById(validRoles[i])))
					return true;
			
			// If not yet valid, inspect for trusted. If trusted, ONLY links in valid channels allowed
			if (!sender.getRoles().contains(Main.getMgl().getRoleById(TRUSTED)))
				return false;
			else if (channelIsValid(event))
				return true;
			
			return false;
		}
	}
	
	// If link is sent in valid channel (for Trusted only)
	private static boolean channelIsValid(GuildMessageReceivedEvent event)
	{
		TextChannel destination = event.getChannel();
		
		for (int i = 0; i < validTextChannels.length; i++)
			if (destination.equals(Main.getMgl().getTextChannelById(validTextChannels[i])))
				return true;
		
		return false;
	}
}