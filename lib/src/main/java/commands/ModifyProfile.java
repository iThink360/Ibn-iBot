/** Event listener for changing profile status, activity, and nickname. */

package commands;

import java.awt.Color;
import java.net.URL;

import javax.annotation.Nonnull;

import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModifyProfile extends ListenerAdapter
{
	private static final Color PROFILE_EMBED = new Color(0xff00ff);
	
	@Override
	public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event)
	{
		if (event.getAuthor().isBot())
			return;
		
		if (!event.getAuthor().equals(Main.getOwner()))
			return;
		
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		
		// Set Activity
		if (args[0].equalsIgnoreCase(Main.getPrefix() + "activity"))
		{
			try
			{
				boolean valid = false;
				String status = "";
				
				for (int i = 2; i < args.length - 1; i++)
					status += args[i] + " ";
				
				try
				{
					@SuppressWarnings("unused")
					URL temp = new URL(args[args.length - 1]);
				}
				catch (Exception e)
				{
					status += args[args.length - 1];
				}
				
				switch (args[1].toLowerCase())
				{
					case "playing":
						event.getJDA().getPresence().setActivity(Activity.playing(status));
						valid = true;
						break;
						
					case "listening":
						event.getJDA().getPresence().setActivity(Activity.listening(status));
						valid = true;
						break;
						
					case "watching":
						event.getJDA().getPresence().setActivity(Activity.watching(status));
						valid = true;
						break;
						
					case "competing":
						event.getJDA().getPresence().setActivity(Activity.competing(status));
						valid = true;
						break;
						
					case "streaming":
						event.getJDA().getPresence().setActivity(Activity.streaming(status, args[args.length - 1]));
						valid = true;
						break;
				}
				
				if (valid)
				{
					EmbedBuilder success = new EmbedBuilder()
							.setColor(PROFILE_EMBED)
							.setTitle("📟 Self JDA Profile Modified")
							.setDescription("Activity Successfully Changed")
							.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
					event.getChannel().sendMessageEmbeds(success.build()).queue();
				}
			}
			catch (Exception e)
			{
				return;
			}
		}
		
		// Set Online Status
		else if (args[0].equalsIgnoreCase(Main.getPrefix() + "status"))
		{
			if (args.length != 2)
				return;
			
			try
			{
				boolean valid = false;
				switch (args[1].toLowerCase())
				{
				case "online":
					event.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
					valid = true;
					break;
					
				case "idle":
					event.getJDA().getPresence().setStatus(OnlineStatus.IDLE);
					valid = true;
					break;
					
				case "dnd":
					event.getJDA().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
					valid = true;
					break;
					
				case "offline":
					event.getJDA().getPresence().setStatus(OnlineStatus.OFFLINE);
					valid = true;
					break;
				}
				
				if (valid)
				{
					EmbedBuilder success = new EmbedBuilder()
							.setColor(PROFILE_EMBED)
							.setTitle("📟 Self JDA Profile Modified")
							.setDescription("Online Status Successfully Changed")
							.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
					event.getChannel().sendMessageEmbeds(success.build()).queue();
				}
			}
			catch (Exception e)
			{
				return;
			}
		}
		
		// Modify Nickname
		else if (args[0].equalsIgnoreCase(Main.getPrefix() + "nickname"))
		{
			try
			{
				String nickname = "";
				for (int i = 1; i < args.length - 1; i++)
					nickname += args[i] + " ";
				nickname += args[args.length - 1];
				
				if (!nickname.equals(""))
				{
					Main.getMgl().getMember(event.getJDA().getSelfUser()).modifyNickname(nickname).queue();
					
					EmbedBuilder success = new EmbedBuilder()
							.setColor(PROFILE_EMBED)
							.setTitle("📟 Self JDA Profile Modified")
							.setDescription(Main.getMgl().getName() + " Nickname Modified")
							.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
					event.getChannel().sendMessageEmbeds(success.build()).queue();
				}
			}
			catch (Exception e)
			{
				return;
			}
		}
	}
}
