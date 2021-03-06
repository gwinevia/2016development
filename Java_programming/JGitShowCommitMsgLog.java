import java.io.File;
import java.io.IOException;
import java.util.*;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.treewalk.*;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm;

public class JGitShowCommitMsgLog {

	static String localPath = "/home/mmk/krswmmk";

	public static void main(String[] args) throws Exception {
		
		FileRepository localRepo = new FileRepository(localPath + "/.git");
    Git git = new Git(localRepo);


    ObjectId headForCommitLog = localRepo.resolve("HEAD");
    // git log
    Iterable<RevCommit> log = git.log().add(headForCommitLog).setMaxCount(1).call();
    for (RevCommit rev : log) {
        System.out.println("commit:\t" + rev.getName() + "\tcommit_msg:" + rev.getShortMessage());
    }


    // Get the id of the tree associated to the two commits
		ObjectId head = localRepo.resolve("HEAD^{tree}");
		ObjectId previousHead = localRepo.resolve("HEAD~^{tree}");
		
		// Instanciate a reader to read the data from the Git database
		ObjectReader reader = localRepo.newObjectReader();
		
		// Create the tree iterator for each commit
		CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
		oldTreeIter.reset(reader, previousHead);
		CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
		newTreeIter.reset(reader, head);
		List<DiffEntry> listDiffs = git.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter).call();
		
		// Simply display the diff between the two commits
		for (DiffEntry diff : listDiffs) {
		  System.out.println(diff);
		  DiffFormatter formatter = new DiffFormatter(System.out);
			formatter.setRepository(localRepo);
			formatter.format(diff);
			System.out.println(diff.getChangeType() + "  " +diff.getNewPath());
		}
  }
}