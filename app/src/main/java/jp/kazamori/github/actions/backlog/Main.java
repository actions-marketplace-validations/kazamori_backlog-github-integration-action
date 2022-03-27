package jp.kazamori.github.actions.backlog;

import com.google.common.annotations.VisibleForTesting;
import com.typesafe.config.ConfigFactory;
import jp.kazamori.github.actions.backlog.client.BacklogClientUtil;
import jp.kazamori.github.actions.backlog.client.GitHubClient;
import jp.kazamori.github.actions.backlog.command.PullRequest;
import jp.kazamori.github.actions.backlog.config.ConfigUtil;
import lombok.val;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

@Command(name = "Backlog GitHub integration action",
        subcommands = {HelpCommand.class},
        description = "provides functionalities to integrate with Backlog")
public class Main {

    @VisibleForTesting
    static String[] ensureArgumentsIsNotQuoted(String[] args) {
        // for GitHub Actions
        // it takes inputs as a single string
        // like "--repository kazamori/backlog-github-integration-action --pr-number 1"
        if (args.length != 1) {
            return args;
        }
        return args[0].split(" ");
    }

    public static void main(String[] args) {
        val config = ConfigFactory.load();
        ConfigUtil.setLogLevel(config);
        val backlogClient = BacklogClientUtil.createClient(config);
        val githubClient = GitHubClient.create(config);
        val exitCode = new CommandLine(new Main())
                .addSubcommand(new PullRequest(backlogClient, githubClient))
                .execute(ensureArgumentsIsNotQuoted(args));
        System.exit(exitCode);
    }
}
