This page is to collect test cases which are ambiguous or in which the Logic has failed parse correctly.


# Format #
New/Accepted/Fixed<br>
Command:<br>
Current Output:<br>
Expected Output: <br>
Commenter Rationale:  (if not self explanatory)<br>
Other Outputs and their rationale....<br>


New<br>
Command: add Go to event before 18 October<br>
Current Output: Deadline task "Go to event" by Thu 18 Oct 2012 10:46PM added<br>
Expected Output: Deadline task "Go to event" by Thu 18 Oct 2012 00:00AM added<br>
KM Rationale: No time specified, why use the current time? Causes issues when you sort stuff too.<br>


New<br>
Command: add go to meeting at noon<br>
Current Output: Deadline task "go to meeting at" by Sun 14 Oct 2012 12:00PM added<br>
Expected Output: Deadline task "go to meeting" by Sun 14 Oct 2012 12:00PM added<br>

New<br>
Command: add School carnival from tomorrow to today<br>
Current Output: Assertion Error by Task object<br>
Expected Output: The start time of this task is after the end time. Please re-enter your entry.<br>

Fixed<br>
Command: add fly to Germany by 2013<br>
Current Output: Deadline task "fly to Germany" by Sun 14 Oct 2012 8:13PM added<br>
KM Rationale: I agree its most likely to mean the time rather than year. Keep it as it is.<br>