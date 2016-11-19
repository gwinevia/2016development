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

public class JGitCheckoutCommitTest {

	static String localPath = "/home/mmk/krswmmk";
	static int test = 0;

	public static void main(String[] args) throws Exception {
		
		FileRepository localRepo = new FileRepository(localPath + "/.git");
    Git git = new Git(localRepo);

    ObjectId headForCommitLog = localRepo.resolve("HEAD");
    // git log
    Iterable<RevCommit> log = git.log().add(headForCommitLog).setMaxCount(2).call();
    for (RevCommit rev : log) {
        System.out.println("commit:\t" + rev.getName() + "\tcommit_msg:" + rev.getShortMessage());
        if(test == 1){
    			git.checkout().setName(rev.getName()).call();
    		}
        test++;
    }

  }
}