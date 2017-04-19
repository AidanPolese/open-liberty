#!/usr/bin/perl -w

my %bugs;
my $bug;

while (<>) {
  if (/<BugInstance type="(.*?)"/) {
    $bug = $1;
  } elsif (/<Class classname="(.*?)"/) {
    my $class = $1;
    ${bugs}{$bug}{$class} = 1;
  }
}

print <<EOF;
<FindBugsFilter>
  <!-- See http://findbugs.sourceforge.net/manual/filter.html for details of the syntax of this file -->
EOF

for my $bug (sort keys %bugs) {
  print "\n";
  print "  <Match>\n";
  my @classes = sort keys %{$bugs{$bug}};
  if (@classes == 1) {
      print "    <Class name=\"$classes[0]\"/>\n";
  } else {
    print "    <Or>\n";
    for my $class (@classes) {
      print "      <Class name=\"$class\"/>\n";
    }
    print "    </Or>\n";
  }
  print "    <Bug pattern=\"$bug\"/>\n";
  print "  </Match>\n";
}

print "</FindBugsFilter>\n";
