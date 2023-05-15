/** Event listener for filtering prohibited language. */

package events;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Automoderator extends ListenerAdapter
{
	private static final Color AUTOMODERATOR_EMBED = new Color(0xffff00);
	private static final String[] illegalWords = {
			"arse",
			"ass",
			"asshole",
			"bastard",
			"bfd",
			"bitch",
			"bollocks",
			"badass",
			"baddass",
			"brotherfucker",
			"bugger",
			"bullshit",
			"childfucker",
			"chode",
			"choad",
			"cunt",
			"dickhead",
			"dilligaf",
			"effing",
			"faggot",
			"fatherfucker",
			"frigger",
			"ffs",
			"fml",
			"fng",
			"fuck",
			"fyfi",
			"gtfo",
			"horseshit",
			"lmao",
			"lmfao",
			"motherfucker",
			"nigger",
			"nigga",
			"penis",
			"prick",
			"pussy",
			"shit",
			"shitass",
			"sisterfucker",
			"slut",
			"stfu",
			"tf",
			"twat",
			"whore",
			"wtf",
			"wtfo"
	};
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		if (!event.getGuild().getName().equals("The IDE"))
		{
			String[] args = event.getMessage().getContentRaw().split("\\s+");
			List<String> badWordsUsed = new ArrayList<String>();
			
			for (String s : illegalWords)
				for (int i = 0; i < args.length; i++)
					if (args[i].equalsIgnoreCase(s))
						badWordsUsed.add(s);
			
			if (badWordsUsed.size() == 0)
				return;
			
			Member felon = event.getMember();
			event.getMessage().delete().queue();
			
			String listOfWordsUsed = "";
			for (int i = 0; i < badWordsUsed.size() - 1; i++)
				listOfWordsUsed += badWordsUsed.get(i) + ", ";
			listOfWordsUsed += badWordsUsed.get(badWordsUsed.size() - 1);
			
			EmbedBuilder DM = new EmbedBuilder()
					.setTitle("⚠ Foul Language Detected")
					.setColor(AUTOMODERATOR_EMBED)
					.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
					.setDescription("Foul language and acronyms are prohibited.")
					.addField("Words Used:", listOfWordsUsed, false);
			
			EmbedBuilder serverMessage = new EmbedBuilder()
					.setDescription("⚠ Watch your mouth!")
					.setColor(AUTOMODERATOR_EMBED);
			
			event.getChannel().sendMessage(felon.getAsMention()).setEmbeds(serverMessage.build()).queue();
			event.getAuthor().openPrivateChannel().complete().sendMessageEmbeds(DM.build()).queue();
		}
	}
}