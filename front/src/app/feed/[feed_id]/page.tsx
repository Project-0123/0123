"use client";

import { useQuery } from "@tanstack/react-query";
import { getFeedDetail } from "@/api/feed";
import useCommentToggleStateStore from "@/stores/comment-toggle";
import { FeedContent, FeedInterface, Comment } from "@/components";
import style from "./feed.module.scss";

type FeedProps = {
  params: { feed_id: number };
};

export default function Feed({ params }: FeedProps) {
  const feedId = params.feed_id;
  const { data } = useQuery({
    queryKey: ["feedDetail", feedId],
    queryFn: () => getFeedDetail(feedId),
  });
  const { commentToggle } = useCommentToggleStateStore();

  return (
    <>
      {data && (
        <>
          <div className={style["feed-container"]}>
            <FeedContent feedData={data} />
            <FeedInterface feedData={data} />
          </div>
          {commentToggle && <Comment />}
        </>
      )}
    </>
  );
}
