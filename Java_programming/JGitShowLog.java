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
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm;

public class JGitShowLog {

	static String localPath = "/home/mmk/krswmmk";

  // blobIdが持つファイルの情報を持つRawTextインスタンスを返すメソッド(Egitのソースより引用)
  private static RawText readText(AbbreviatedObjectId blobId,ObjectReader reader) throws IOException {
    ObjectLoader oldLoader = reader.open(blobId.toObjectId(),Constants.OBJ_BLOB);
    return new RawText(oldLoader.getCachedBytes());
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
    Iterable<RevCommit> log = git.log().all().call();
    for (RevCommit rev : log) {
        //System.out.println("commit:\t" + rev.getName().substring(0, 7) + "\tcommit_msg:" + rev.getShortMessage());
    }

    DiffFormatter diffFormatter = new DiffFormatter(System.out);
    diffFormatter.setRepository(localRepo);

    String to = args[0];
        
    RevWalk walk = new RevWalk(localRepo);
    RevCommit fromCommit = walk.parseCommit(localRepo.resolve("HEAD"));
    RevCommit toCommit = walk.parseCommit(localRepo.resolve(to));
    RevTree fromTree = fromCommit.getTree();
    RevTree toTree = toCommit.getTree();
    List<DiffEntry> list = diffFormatter.scan(toTree, fromTree);
    list.forEach((diffEntry) -> {
        //System.out.printf("%s\t%s\n", diffEntry.getChangeType(),diffEntry.getNewPath());
        try{
            if (diffEntry.getChangeType() != DiffEntry.ChangeType.DELETE &&
                diffEntry.getChangeType() != DiffEntry.ChangeType.ADD) {
                RawText oldText = readText(diffEntry.getOldId(), reader);
                RawText newText = readText(diffEntry.getNewId(), reader);
                EditList editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, oldText, newText);
                for (Edit edit : editList) {
                    System.out.println(diffEntry.getNewPath()
                            +" " + diffEntry.getChangeType()
                            + "\n old \n"
                            + oldText.getString(edit.getBeginA(),
                                    edit.getEndA(), false)
                            + "\n\n new \n"
                            + newText.getString(edit.getBeginB(),
                                    edit.getEndB(), false));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
    walk.dispose();
	}
}
