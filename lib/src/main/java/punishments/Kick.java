/** Event listener for kicking members. */

package punishments;

import java.awt.Color;
import java.time.Instant;

import javax.annotation.Nonnull;

import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Kick extends ListenerAdapter
{
	private static final Color KICK_EMBED = new Color(0xff8800);
	
	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		Member sender = event.getMember();
		
		if (!args[0].equalsIgnoreCase(Main.getPrefix() + "kick"))
			return;
		
		if (!sender.isOwner() && !sender.hasPermission(Permission.ADMINISTRATOR) && !sender.hasPermission(Permission.KICK_MEMBERS))
		{
			// Denied
			EmbedBuilder denied = new EmbedBuilder()
					.setColor(KICK_EMBED)
					.setDescription("❌ " + sender.getUser().getAsTag() + ", nice try. You can't use that.");
			event.getChannel().sendMessageEmbeds(denied.build()).queue();
			return;
		}
		
		if (args.length <= 1)
			return;
		
		if (args[1].equalsIgnoreCase("help"))
		{
			// Help
			EmbedBuilder help = new EmbedBuilder()
					.setColor(KICK_EMBED)
					.setTitle("👢 Kick")
					.setDescription("Kicks a member.")
					.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
					.addField("Command:", "`" + Main.getPrefix() + "kick [target ID] <reason>`", false)
					.addField("`[target ID]`", "-The ID of the target who will get kicked.\n-Omit [] when specifying this value.", true)
					.addField("`<reason>`", "-The reason for the kick.\n-This field is optional. When specifying the reason, omit <>.", true)
					.setFooter(event.getGuild().getName(), event.getGuild().getIconUrl());
			event.getChannel().sendMessageEmbeds(help.build()).queue();
			return;
		}
		
		if (args[1].equals("@everyone"))
		{
			// Are You Sure?
			EmbedBuilder areYouSure = new EmbedBuilder()
					.setColor(KICK_EMBED)
					.setDescription("‼ **Are You Sure?**");
			event.getChannel().sendMessageEmbeds(areYouSure.build()).queue();
			return;
		}
		
		try
		{
			Member target = event.getGuild().retrieveMemberById(args[1]).complete();
			
			if (target.equals(sender))
			{
				// Striking Yourself?
				EmbedBuilder idiot = new EmbedBuilder()
						.setColor(KICK_EMBED)
						.setDescription("❌ Use common sense; you can't kick yourself.");
				event.getChannel().sendMessageEmbeds(idiot.build()).queue();
			}
			else if (target.isOwner() || target.hasPermission(Permission.ADMINISTRATOR) || target.hasPermission(Permission.KICK_MEMBERS))
			{
				// Illegal Target
				EmbedBuilder error = new EmbedBuilder()
						.setColor(KICK_EMBED)
						.setDescription("❌ You cannot kick this member.");
				event.getChannel().sendMessageEmbeds(error.build()).queue();
			}
			else
			{
				// Success
				EmbedBuilder kick = new EmbedBuilder()
						.setColor(KICK_EMBED)
						.setDescription("👢 **" + target.getUser().getAsTag() + "** was kicked.");
						
				String reason = "";
				if (args.length > 2)
				{
					for (int i = 2; i < args.length - i; i++)
						reason += args[i] + " ";
					reason += args[args.length - 1];
				}
				
				target.kick(reason).queue();
				event.getChannel().sendMessageEmbeds(kick.build()).queue();
				
				// DM the target
				EmbedBuilder DM = new EmbedBuilder()
						.setTitle("👢 You have been kicked.")
						.setColor(KICK_EMBED)
						.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
				if (!reason.equals(""))
					DM.addField("Reason:", reason, false);
				target.getUser().openPrivateChannel().complete().sendMessageEmbeds(DM.build()).queue();
				
				// Log the kick
				EmbedBuilder log = new EmbedBuilder()
						.setTitle("👢 Kick Log")
						.setColor(KICK_EMBED)
						.addField("Moderator:", sender.getAsMention(), false)
						.addField("Perpetrator:", target.getAsMention(), true)
						.addField("Perpetrator (Raw Tag):", target.getUser().getAsTag(), true)
						.setFooter("Perpetrator ID: " + target.getId())
						.setTimestamp(Instant.now());
				if (!reason.equals(""))
					log.addField("Reason:", reason, false);
				Main.getMglInfractions().sendMessageEmbeds(log.build()).queue();
			}
		}
		catch (Exception e)
		{
			// Error
			EmbedBuilder error = new EmbedBuilder()
					.setColor(KICK_EMBED)
					.setTitle("❗ Kick Error")
					.setDescription("You may have used the command incorrectly. Please try again. For help on kicking members, send `" + Main.getPrefix() + "kick help`.")
					.addField("Exception Thrown:", e.toString(), false);
			event.getChannel().sendMessageEmbeds(error.build()).queue();
		}
	}
}
