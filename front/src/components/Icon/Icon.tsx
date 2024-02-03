import Image from "next/image";

type IconType =
  | "add"
  | "addFile"
  | "artist"
  | "befoteBookmark"
  | "afterBookmark"
  | "checkedBookmark"
  | "filter"
  | "filterBlack"
  | "cancel"
  | "copy"
  | "copyBlack"
  | "leftArrow"
  | "rightArrow"
  | "bforeLike"
  | "afterLike"
  | "alignCenter"
  | "alignRight"
  | "alignLeft"
  | "beforeFollow"
  | "afterFollow"
  | "comment";

interface Props {
  iconName: IconType;
  size?: number;
}

export default function Icon({ iconName, size = 24 }: Props) {
  const iconPath = `/icons/${iconName}.svg`;
  return (
    <Image
      src={iconPath}
      alt={iconName}
      width={size}
      height={size}
      title={`Icon: ${iconName as string}`}
    />
  );
}
