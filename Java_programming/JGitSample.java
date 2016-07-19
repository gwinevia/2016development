import java.io.File;
import java.io.IOException;
import java.util.*;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

public class JGitSample {

	static String localPath = "/home/mmk/krswmmk";

	public static void main(String[] args) throws Exception {

    // ローカルリポジトリの指定など
		FileRepository localRepo = new FileRepository(localPath + "/.git");
		
		// Git オブジェクト作成 (このオブジェクトを操作していろいろする)
    Git git = new Git(localRepo);

  	git.add().addFilepattern("Java_programming").call();
    git.commit().setMessage("JGitSample add&commit test!").call();
	}
}