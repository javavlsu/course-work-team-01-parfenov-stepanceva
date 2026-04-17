import { cn } from '../../utils/cn'

export function Skeleton({ className }) {
  return <div aria-busy="true" className={cn('skeleton rounded-md', className)} />
}
