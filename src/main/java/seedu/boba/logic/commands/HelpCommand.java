package seedu.boba.logic.commands;

import seedu.boba.model.BobaBotModel;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Example: " + COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    @Override
    public CommandResult execute(BobaBotModel bobaBotModel) {
        return new CommandResult(SHOWING_HELP_MESSAGE, true, false, false, false, false);
    }
}
