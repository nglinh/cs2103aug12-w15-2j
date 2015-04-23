Pushing to Undo Stack Procedure for Handlers which modify database

For commands that modify the database, normally, the old database is cloned before it is edited and written. There could be an error during the parsing or file writing process. To protect against breaking the undo operation or storing an unwanted clone. The procedure to do so is detailed below.


# Details #

1. Get current taskList, _List_

&lt;Task&gt;

 currentTaskList = super.getCurrentTaskList();_**Put this as the first line inside the try block.**_

2. Conduct necessary modification operation

3. **Place this statement as the second-last line inside the try block**, just before you return LogicToUi. _super.pushUndoStatusMessageAndTaskList(undoMessage, currentTaskList);_
undoMessage refers to what you want the user to see if the step is undone. The Undo Handler will display the message in this manner.

"The " + undoMessage + " has been undone"

So phrase your message as if it is inside there.

If any exceptions are thrown, this line will not be executed and thus will not be pushed to undo stack.


There is no need to write to the database object. If there is an error, the database class will retain the old version of the list.