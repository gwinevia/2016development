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

	private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws Exception {
	    Ref head = repository.getRef(ref);
	    RevWalk walk = new RevWalk(repository);
	    RevCommit commit = walk.parseCommit(head.getObjectId());
	    RevTree tree = walk.parseTree(commit.getTree().getId());
	
	    CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
	    ObjectReader oldReader = repository.newObjectReader();
	    oldTreeParser.reset(oldReader, tree.getId());

	    return oldTreeParser;
	}

	public static void main(String[] args) throws Exception {
		
		// ローカルリポジトリの指定
		FileRepository localRepo = new FileRepository(localPath + "/.git");
		
		// Git オブジェクト作成 (このオブジェクトを操作していろいろする)
    Git git = new Git(localRepo);

    DiffAlgorithm diffAlgorithm = DiffAlgorithm.getAlgorithm(
    								localRepo.getConfig().getEnum(ConfigConstants.CONFIG_DIFF_SECTION,
                    null, ConfigConstants.CONFIG_KEY_ALGORITHM,
                    SupportedAlgorithm.HISTOGRAM));
    ObjectReader reader = localRepo.newObjectReader();

    // git log
    ObjectId head = localRepo.resolve(Constants.HEAD);
    Iterable<RevCommit> log = git.log().add(head).setMaxCount(1).call();
    for (RevCommit rev : log) {
        //System.out.println("commit:\t" + rev.getName() + "\tcommit_msg:" + rev.getShortMessage());
    }

    DiffFormatter diffFormatter = new DiffFormatter(System.out);
    diffFormatter.setRepository(localRepo);

		AbstractTreeIterator commitTreeIterator = prepareTreeParser(localRepo,Constants.HEAD);
		FileTreeIterator workTreeIterator = new FileTreeIterator(localRepo);
		List<DiffEntry> diffEntries = diffFormatter.scan(commitTreeIterator, workTreeIterator);
		
		for( DiffEntry entry : diffEntries ) {
		  System.out.println( "Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId() );
		  diffFormatter.format(entry);
		}
  }
}